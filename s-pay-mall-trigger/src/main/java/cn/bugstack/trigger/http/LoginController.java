package cn.bugstack.trigger.http;

import cn.bugstack.api.response.Response;
import cn.bugstack.domain.auth.service.IAccountService;
import cn.bugstack.domain.auth.service.ILoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {
    private final ILoginService loginService;
    private final IAccountService accountService;
    public LoginController(ILoginService loginService, IAccountService accountService) { this.loginService = loginService; this.accountService = accountService; }

    @GetMapping("/weixin_qrcode_ticket") public Response<String> weixinQrCodeTicket() { return Response.success(loginService.createQrCodeTicket()); }
    @GetMapping("/check_login") public Response<String> checkLogin(@RequestParam String ticket) {
        String openId = loginService.checkLogin(ticket);
        if (StringUtils.isBlank(openId)) return Response.failure("AUTH_WAIT", "等待扫码");
        return Response.success(accountService.loginWithWechat(openId));
    }
}
