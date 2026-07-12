package cn.bugstack.infrastructure.payment;

import cn.bugstack.domain.mall.adapter.port.IPaymentPort;
import cn.bugstack.domain.mall.model.entity.MallOrderEntity;
import cn.bugstack.domain.mall.model.entity.PaymentCreateResultEntity;
import cn.bugstack.domain.mall.model.entity.PaymentQueryResultEntity;
import cn.bugstack.domain.mall.model.valobj.PaymentStatusVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.bugstack.types.utils.SignatureUtil;

@Component
@ConditionalOnProperty(name = "alipay.enabled", havingValue = "false", matchIfMissing = true)
public class MockPaymentPort implements IPaymentPort {
    @Value("${app.backend-public-url:http://localhost:8091}") private String backendPublicUrl;
    @Value("${mock-payment.secret}") private String mockPaymentSecret;
    @Override
    public PaymentCreateResultEntity createPagePayment(MallOrderEntity order) {
        String signature = SignatureUtil.hmacSha256(mockPaymentSecret, order.getOrderNo());
        String html = "<!doctype html><html><head><meta charset='utf-8'><title>模拟支付</title>" +
                "<style>body{font-family:sans-serif;display:grid;place-items:center;height:100vh;background:#f5f7fa}.card{background:#fff;padding:36px;border-radius:12px;box-shadow:0 8px 30px #0001}button{padding:12px 28px;background:#1677ff;color:white;border:0;border-radius:8px;cursor:pointer}</style></head>" +
                "<body><div class='card'><h2>本地模拟支付</h2><p>订单：" + order.getOrderNo() + "</p><p>金额：¥" + order.getPayAmount() + "</p>" +
                "<form method='post' action='" + backendPublicUrl + "/api/v1/payments/mock/" + order.getOrderNo() + "/success?signature=" + signature + "'><button type='submit'>确认支付</button></form></div></body></html>";
        return PaymentCreateResultEntity.builder().orderNo(order.getOrderNo()).paymentNo("MOCK" + order.getOrderNo()).htmlForm(html).build();
    }
    @Override public PaymentQueryResultEntity queryPayment(String orderNo) { return PaymentQueryResultEntity.builder().status(PaymentStatusVO.PAYING).rawStatus("MOCK_WAITING").build(); }
    @Override public boolean closePayment(String orderNo) { return true; }
}
