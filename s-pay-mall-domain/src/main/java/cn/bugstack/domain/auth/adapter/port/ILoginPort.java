package cn.bugstack.domain.auth.adapter.port;

import java.io.IOException;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 登录适配器接口
 * @create 2024-02-25 12:03
 */
public interface ILoginPort {

    String createQrCodeTicket() throws IOException;

    String queryLoginState(String ticket);

    void saveLoginState(String ticket, String openid);

    void sendLoginTempleteMessage(String openid) throws IOException;

}
