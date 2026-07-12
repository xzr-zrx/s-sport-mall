package cn.bugstack.trigger.http;

import cn.bugstack.domain.mall.model.entity.PaymentCreateResultEntity;
import cn.bugstack.domain.mall.service.IMallService;
import cn.bugstack.trigger.support.CurrentUser;
import cn.bugstack.types.utils.SignatureUtil;
import com.alipay.api.internal.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final IMallService mallService;
    @Value("${alipay.enabled:false}") private boolean alipayEnabled;
    @Value("${alipay.app-id:}") private String appId;
    @Value("${alipay.alipay-public-key:}") private String alipayPublicKey;
    @Value("${alipay.seller-id:}") private String sellerId;
    @Value("${alipay.charset:utf-8}") private String charset;
    @Value("${alipay.sign-type:RSA2}") private String signType;
    @Value("${mock-payment.secret}") private String mockPaymentSecret;
    @Value("${app.frontend-public-url:http://localhost:5173}") private String frontendPublicUrl;

    public PaymentController(IMallService mallService) { this.mallService = mallService; }

    @PostMapping(value="/alipay/{orderNo}", produces=MediaType.TEXT_HTML_VALUE)
    public String create(@PathVariable String orderNo) {
        PaymentCreateResultEntity result = mallService.createPayment(CurrentUser.id(), orderNo);
        return result.getHtmlForm();
    }

    @PostMapping(value="/alipay/notify", produces=MediaType.TEXT_PLAIN_VALUE)
    public String notify(HttpServletRequest request) {
        Map<String,String> params = extractParams(request);
        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String hash = hash(params);
        try {
            if (!alipayEnabled) return "failure";
            if (!AlipaySignature.rsaCheckV1(params, alipayPublicKey, charset, signType)) throw new IllegalArgumentException("验签失败");
            if (!Objects.equals(appId, params.get("app_id"))) throw new IllegalArgumentException("应用编号不匹配");
            if (sellerId != null && !sellerId.isBlank() && !Objects.equals(sellerId, params.get("seller_id"))) throw new IllegalArgumentException("收款方不匹配");
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) throw new IllegalArgumentException("交易状态无效");
            if (!mallService.verifyNotify(orderNo, new BigDecimal(params.get("total_amount")))) throw new IllegalArgumentException("订单金额不匹配");
            mallService.handlePaymentSuccess(orderNo, tradeNo, hash);
            return "success";
        } catch (Exception e) {
            log.error("payment notify rejected orderNo={} reason={}", orderNo, e.getMessage());
            try { mallService.recordNotifyFailure(orderNo, tradeNo, hash, e.getMessage()); } catch (Exception logError) { log.error("failed to persist notify failure", logError); }
            return "failure";
        }
    }

    @PostMapping(value="/mock/{orderNo}/success", produces=MediaType.TEXT_HTML_VALUE)
    public String mockSuccess(@PathVariable String orderNo, @RequestParam String signature) {
        if (alipayEnabled) return "<h3>Mock payment is disabled.</h3>";
        String expected = SignatureUtil.hmacSha256(mockPaymentSecret, orderNo);
        if (!SignatureUtil.constantTimeEquals(expected, signature)) throw new IllegalArgumentException("模拟支付签名无效");
        mallService.handlePaymentSuccess(orderNo, "MOCK-" + orderNo, "MOCK");
        String safeOrderNo = orderNo.replaceAll("[^0-9A-Za-z_-]", "");
        return "<!doctype html><html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'><title>支付成功</title>" +
                "<style>body{margin:0;font-family:Inter,system-ui,sans-serif;background:#f6f8fa;color:#17212b;display:grid;place-items:center;min-height:100vh}.card{width:min(88vw,440px);padding:40px;background:#fff;border:1px solid #e4e7ec;border-radius:22px;box-shadow:0 18px 50px #1018281c;text-align:center}.icon{width:68px;height:68px;margin:0 auto 20px;border-radius:50%;display:grid;place-items:center;background:#ecfdf3;color:#0c9b69;font-size:34px}h2{margin:0 0 10px}p{color:#667085;line-height:1.7}a{display:inline-block;margin-top:12px;padding:12px 22px;border-radius:10px;background:#ff4f2e;color:#fff;text-decoration:none;font-weight:700}</style></head>" +
                "<body><div class='card'><div class='icon'>✓</div><h2>模拟支付成功</h2><p>订单 " + safeOrderNo + " 已完成支付，商城页面将自动同步订单状态。</p><a href='" + frontendPublicUrl + "/payment-result?orderNo=" + safeOrderNo + "'>返回商城</a></div>" +
                "<script>if(window.opener){window.opener.postMessage({type:'S_PAY_PAYMENT_SUCCESS',orderNo:'" + safeOrderNo + "'},'*')}setTimeout(function(){if(window.opener)window.close()},1600)</script></body></html>";
    }

    private Map<String,String> extractParams(HttpServletRequest request) {
        Map<String,String> result = new TreeMap<>();
        request.getParameterMap().forEach((k,v) -> result.put(k, String.join(",", v)));
        return result;
    }
    private String hash(Map<String,String> params) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(params.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(); for (byte b : bytes) sb.append(String.format("%02x", b)); return sb.toString();
        } catch (Exception e) { return "HASH_ERROR"; }
    }
}
