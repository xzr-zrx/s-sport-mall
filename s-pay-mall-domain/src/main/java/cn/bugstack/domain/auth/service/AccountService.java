package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.adapter.port.ITokenPort;
import cn.bugstack.domain.auth.adapter.repository.IUserRepository;
import cn.bugstack.domain.auth.model.entity.UserAccountEntity;
import cn.bugstack.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements IAccountService {

    private final IUserRepository userRepository;
    private final ITokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;

    public AccountService(IUserRepository userRepository, ITokenPort tokenPort, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String register(String username, String password, String nickname) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new AppException("AUTH_001", "用户名和密码不能为空");
        }
        username = username.trim();
        if (userRepository.findByUsername(username) != null) {
            throw new AppException("AUTH_002", "用户名已存在");
        }
        UserAccountEntity user = UserAccountEntity.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .nickname(StringUtils.defaultIfBlank(nickname, username))
                .role("USER")
                .status("ENABLED")
                .build();
        user.setId(userRepository.save(user));
        return tokenPort.issueToken(user);
    }

    @Override
    public String login(String username, String password) {
        UserAccountEntity user = userRepository.findByUsername(username);
        if (user == null || !"ENABLED".equals(user.getStatus()) || StringUtils.isBlank(user.getPasswordHash()) || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AppException("AUTH_003", "用户名或密码错误");
        }
        return tokenPort.issueToken(user);
    }

    @Override
    @Transactional
    public String loginWithWechat(String openId) {
        if (StringUtils.isBlank(openId)) {
            throw new AppException("AUTH_004", "微信登录信息无效");
        }
        return tokenPort.issueToken(userRepository.findOrCreateByWechatOpenId(openId));
    }

    @Override
    public UserAccountEntity getUser(Long userId) {
        UserAccountEntity user = userRepository.findById(userId);
        if (user == null) throw new AppException("AUTH_005", "用户不存在");
        user.setPasswordHash(null);
        return user;
    }
}
