package cn.bugstack.infrastructure.adapter.port;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;
import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import cn.bugstack.infrastructure.gateway.dto.*;
import cn.bugstack.infrastructure.redis.IRedisService;
import cn.bugstack.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginPort implements ILoginPort {
    private static final String ACCESS_TOKEN_KEY = "weixin:access_token:";
    private static final String LOGIN_STATE_KEY = "login:ticket:";
    private static final long LOGIN_TTL = TimeUnit.MINUTES.toMillis(10);

    @Value("${weixin.config.enabled:false}") private boolean enabled;
    @Value("${weixin.config.app-id:}") private String appId;
    @Value("${weixin.config.app-secret:}") private String appSecret;
    @Value("${weixin.config.template-id:}") private String templateId;
    @Value("${weixin.config.portal-url:http://localhost:5173}") private String portalUrl;

    private final IRedisService redisService;
    private final IWeixinApiService api;

    public LoginPort(IRedisService redisService, IWeixinApiService api) { this.redisService = redisService; this.api = api; }

    @Override
    public String createQrCodeTicket() throws IOException {
        ensureConfigured();
        WeixinQrCodeRequestDTO req = WeixinQrCodeRequestDTO.builder().expire_seconds(600)
                .action_name(WeixinQrCodeRequestDTO.ActionNameTypeVO.QR_STR_SCENE.getCode())
                .action_info(WeixinQrCodeRequestDTO.ActionInfo.builder().scene(
                        WeixinQrCodeRequestDTO.ActionInfo.Scene.builder().scene_str("login_" + System.currentTimeMillis()).build()).build()).build();
        Response<WeixinQrCodeResponseDTO> response = api.createQrCode(getAccessToken(), req).execute();
        if (!response.isSuccessful() || response.body() == null || StringUtils.isBlank(response.body().getTicket())) {
            throw new AppException("WEIXIN_001", "生成微信二维码失败");
        }
        return response.body().getTicket();
    }

    @Override public String queryLoginState(String ticket) { return redisService.getValue(LOGIN_STATE_KEY + ticket); }
    @Override public void saveLoginState(String ticket, String openid) { redisService.setValue(LOGIN_STATE_KEY + ticket, openid, LOGIN_TTL); }

    @Override
    public void sendLoginTempleteMessage(String openid) throws IOException {
        if (!enabled || StringUtils.isBlank(templateId)) return;
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.USER, openid);
        WeixinTemplateMessageDTO message = new WeixinTemplateMessageDTO(openid, templateId);
        message.setUrl(portalUrl); message.setData(data);
        api.sendMessage(getAccessToken(), message).execute();
    }

    private String getAccessToken() throws IOException {
        ensureConfigured();
        String key = ACCESS_TOKEN_KEY + appId;
        String cached = redisService.getValue(key);
        if (StringUtils.isNotBlank(cached)) return cached;
        Response<WeixinTokenResponseDTO> response = api.getToken("client_credential", appId, appSecret).execute();
        if (!response.isSuccessful() || response.body() == null || StringUtils.isBlank(response.body().getAccess_token())) {
            throw new AppException("WEIXIN_002", "获取微信 access_token 失败");
        }
        int expires = Math.max(response.body().getExpires_in() - 200, 60);
        redisService.setValue(key, response.body().getAccess_token(), TimeUnit.SECONDS.toMillis(expires));
        return response.body().getAccess_token();
    }

    private void ensureConfigured() {
        if (!enabled || StringUtils.isAnyBlank(appId, appSecret)) throw new AppException("WEIXIN_DISABLED", "微信扫码登录未配置");
    }
}
