package cn.bugstack.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAccount {
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private String role;
    private String wechatOpenId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
