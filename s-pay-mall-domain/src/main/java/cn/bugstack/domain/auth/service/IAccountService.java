package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.model.entity.UserAccountEntity;

public interface IAccountService {
    String register(String username, String password, String nickname);
    String login(String username, String password);
    String loginWithWechat(String openId);
    UserAccountEntity getUser(Long userId);
}
