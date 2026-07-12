package cn.bugstack.trigger.http;

import cn.bugstack.domain.auth.service.ILoginService;
import cn.bugstack.types.sdk.weixin.MessageTextEntity;
import cn.bugstack.types.sdk.weixin.SignatureUtil;
import cn.bugstack.types.sdk.weixin.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/weixin/portal")
public class WeixinPortalController {
    @Value("${weixin.config.originalid:}") private String originalId;
    @Value("${weixin.config.verify-token:}") private String verifyToken;
    private final ILoginService loginService;

    public WeixinPortalController(ILoginService loginService) { this.loginService = loginService; }

    @GetMapping(value="/receive", produces="text/plain;charset=utf-8")
    public String validate(@RequestParam String signature, @RequestParam String timestamp,
                           @RequestParam String nonce, @RequestParam String echostr) {
        return verify(signature, timestamp, nonce) ? echostr : "";
    }

    @PostMapping(value="/receive", produces="application/xml;charset=UTF-8")
    public String receive(@RequestBody String body, @RequestParam String signature,
                          @RequestParam String timestamp, @RequestParam String nonce,
                          @RequestParam String openid) {
        if (!verify(signature, timestamp, nonce)) return "";
        try {
            MessageTextEntity message = XmlUtil.xmlToBean(body, MessageTextEntity.class);
            if ("event".equals(message.getMsgType()) && ("SCAN".equals(message.getEvent()) || "subscribe".equals(message.getEvent()))
                    && StringUtils.isNotBlank(message.getTicket())) {
                loginService.saveLoginState(message.getTicket(), openid);
                return text(openid, "登录成功，可以返回商城继续操作。");
            }
            return text(openid, "请在商城登录页面扫码。");
        } catch (Exception e) {
            log.error("weixin callback failed openid={}", openid, e);
            return "";
        }
    }

    private boolean verify(String signature, String timestamp, String nonce) {
        return StringUtils.isNoneBlank(verifyToken, signature, timestamp, nonce)
                && SignatureUtil.check(verifyToken, signature, timestamp, nonce);
    }

    private String text(String openid, String content) {
        MessageTextEntity response = new MessageTextEntity();
        response.setFromUserName(originalId); response.setToUserName(openid);
        response.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        response.setMsgType("text"); response.setContent(content);
        return XmlUtil.beanToXml(response);
    }
}
