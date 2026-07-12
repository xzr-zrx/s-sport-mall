package cn.bugstack.trigger.http;

import cn.bugstack.api.dto.auth.LoginRequestDTO;
import cn.bugstack.api.dto.auth.RegisterRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.auth.service.IAccountService;
import cn.bugstack.trigger.support.CurrentUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IAccountService accountService;
    public AuthController(IAccountService accountService) { this.accountService = accountService; }

    @PostMapping("/register") public Response<TokenVO> register(@Valid @RequestBody RegisterRequestDTO req) {
        return Response.success(new TokenVO(accountService.register(req.getUsername(), req.getPassword(), req.getNickname())));
    }
    @PostMapping("/login") public Response<TokenVO> login(@Valid @RequestBody LoginRequestDTO req) {
        return Response.success(new TokenVO(accountService.login(req.getUsername(), req.getPassword())));
    }
    @GetMapping("/me") public Response<?> me() { return Response.success(accountService.getUser(CurrentUser.id())); }

    @Data @AllArgsConstructor public static class TokenVO { private String token; }
}
