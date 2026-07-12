package cn.bugstack.domain.auth.adapter.repository;

import cn.bugstack.domain.auth.model.entity.UserAccountEntity;

public interface IUserRepository {
    UserAccountEntity findByUsername(String username);
    UserAccountEntity findById(Long id);
    UserAccountEntity findOrCreateByWechatOpenId(String openId);
    Long save(UserAccountEntity user);
}
