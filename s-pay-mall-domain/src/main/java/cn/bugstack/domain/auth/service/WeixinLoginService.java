package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class WeixinLoginService implements ILoginService {
    private final ILoginPort loginPort;
    public WeixinLoginService(ILoginPort loginPort) { this.loginPort = loginPort; }

    @Override
    public String createQrCodeTicket() {
        try { return loginPort.createQrCodeTicket(); }
        catch (Exception e) { throw new AppException("WEIXIN_QR_ERROR", e.getMessage(), e); }
    }

    @Override public String checkLogin(String ticket) { return loginPort.queryLoginState(ticket); }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        loginPort.saveLoginState(ticket, openid);
        try { loginPort.sendLoginTempleteMessage(openid); }
        catch (Exception e) { log.warn("weixin template message failed openid={}", openid, e); }
    }
}
