package cn.bugstack.infrastructure.repository;

import cn.bugstack.domain.auth.adapter.repository.IUserRepository;
import cn.bugstack.domain.auth.model.entity.UserAccountEntity;
import cn.bugstack.infrastructure.dao.IUserDao;
import cn.bugstack.infrastructure.dao.po.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepository implements IUserRepository {

    private final IUserDao userDao;

    public UserRepository(IUserDao userDao) { this.userDao = userDao; }

    @Override public UserAccountEntity findByUsername(String username) { return toEntity(userDao.findByUsername(username)); }
    @Override public UserAccountEntity findById(Long id) { return toEntity(userDao.findById(id)); }

    @Override
    public UserAccountEntity findOrCreateByWechatOpenId(String openId) {
        UserAccount account = userDao.findByWechatOpenId(openId);
        if (account != null) return toEntity(account);
        UserAccountEntity entity = UserAccountEntity.builder()
                .username("wx_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .nickname("微信用户")
                .role("USER")
                .wechatOpenId(openId)
                .status("ENABLED")
                .build();
        entity.setId(save(entity));
        return entity;
    }

    @Override
    public Long save(UserAccountEntity user) {
        UserAccount po = new UserAccount();
        po.setUsername(user.getUsername()); po.setPasswordHash(user.getPasswordHash()); po.setNickname(user.getNickname());
        po.setRole(user.getRole()); po.setWechatOpenId(user.getWechatOpenId()); po.setStatus(user.getStatus());
        userDao.insert(po);
        return po.getId();
    }

    private UserAccountEntity toEntity(UserAccount po) {
        if (po == null) return null;
        return UserAccountEntity.builder().id(po.getId()).username(po.getUsername()).passwordHash(po.getPasswordHash())
                .nickname(po.getNickname()).role(po.getRole()).wechatOpenId(po.getWechatOpenId()).status(po.getStatus()).build();
    }
}
