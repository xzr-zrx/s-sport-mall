package cn.bugstack.domain.auth.adapter.port;

import cn.bugstack.domain.auth.model.entity.UserAccountEntity;

public interface ITokenPort {
    String issueToken(UserAccountEntity user);
}
