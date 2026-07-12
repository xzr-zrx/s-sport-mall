package cn.bugstack.infrastructure.payment;

import cn.bugstack.domain.mall.adapter.port.IPaymentPort;
import cn.bugstack.domain.mall.model.entity.MallOrderEntity;
import cn.bugstack.domain.mall.model.entity.PaymentCreateResultEntity;
import cn.bugstack.domain.mall.model.entity.PaymentQueryResultEntity;
import cn.bugstack.domain.mall.model.valobj.PaymentStatusVO;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "alipay.enabled", havingValue = "true")
public class AlipayPaymentPort implements IPaymentPort {

    private final AlipayClient alipayClient;

    @Value("${alipay.notify-url}") private String notifyUrl;
    @Value("${alipay.return-url}") private String returnUrl;

    public AlipayPaymentPort(AlipayClient alipayClient) { this.alipayClient = alipayClient; }

    @Override
    public PaymentCreateResultEntity createPagePayment(MallOrderEntity order) {
        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl(returnUrl);
            JSONObject biz = new JSONObject();
            biz.put("out_trade_no", order.getOrderNo());
            biz.put("total_amount", order.getPayAmount().toPlainString());
            String subject = order.getItems() == null || order.getItems().isEmpty() ? "商城订单" : order.getItems().get(0).getProductName();
            biz.put("subject", subject);
            biz.put("product_code", "FAST_INSTANT_TRADE_PAY");
            biz.put("timeout_express", "30m");
            request.setBizContent(biz.toJSONString());
            String html = alipayClient.pageExecute(request).getBody();
            return PaymentCreateResultEntity.builder().orderNo(order.getOrderNo())
                    .paymentNo("PAY" + order.getOrderNo()).htmlForm(html).build();
        } catch (AlipayApiException e) {
            throw new AppException("PAY_GATEWAY_001", "创建支付宝支付失败", e);
        }
    }

    @Override
    public PaymentQueryResultEntity queryPayment(String orderNo) {
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject biz = new JSONObject(); biz.put("out_trade_no", orderNo); request.setBizContent(biz.toJSONString());
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                return PaymentQueryResultEntity.builder().status(PaymentStatusVO.UNKNOWN).rawStatus(response.getSubCode()).build();
            }
            String tradeStatus = response.getTradeStatus();
            PaymentStatusVO status;
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) status = PaymentStatusVO.SUCCESS;
            else if ("WAIT_BUYER_PAY".equals(tradeStatus)) status = PaymentStatusVO.PAYING;
            else if ("TRADE_CLOSED".equals(tradeStatus)) status = PaymentStatusVO.CLOSED;
            else status = PaymentStatusVO.UNKNOWN;
            return PaymentQueryResultEntity.builder().status(status).thirdPartyTradeNo(response.getTradeNo()).rawStatus(tradeStatus).build();
        } catch (AlipayApiException e) {
            return PaymentQueryResultEntity.builder().status(PaymentStatusVO.UNKNOWN).rawStatus(e.getMessage()).build();
        }
    }

    @Override
    public boolean closePayment(String orderNo) {
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            JSONObject biz = new JSONObject(); biz.put("out_trade_no", orderNo); request.setBizContent(biz.toJSONString());
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            return response.isSuccess() || "ACQ.TRADE_NOT_EXIST".equals(response.getSubCode());
        } catch (AlipayApiException e) {
            return false;
        }
    }
}
