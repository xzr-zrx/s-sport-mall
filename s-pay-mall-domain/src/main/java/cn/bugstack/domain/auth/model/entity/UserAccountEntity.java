package cn.bugstack.domain.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountEntity {
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private String role;
    private String wechatOpenId;
    private String status;
}
