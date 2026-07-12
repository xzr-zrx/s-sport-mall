package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IUserDao {
    UserAccount findByUsername(@Param("username") String username);
    UserAccount findById(@Param("id") Long id);
    UserAccount findByWechatOpenId(@Param("openId") String openId);
    int insert(UserAccount user);
    int bindWechat(@Param("id") Long id, @Param("openId") String openId);
}
