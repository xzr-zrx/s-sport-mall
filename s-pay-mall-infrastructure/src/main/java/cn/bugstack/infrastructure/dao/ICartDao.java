package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ICartDao {
    List<CartItem> listByUser(@Param("userId") Long userId);
    List<CartItem> listByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);
    int upsert(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") int quantity);
    int updateQuantity(@Param("userId") Long userId, @Param("id") Long id, @Param("quantity") int quantity);
    int delete(@Param("userId") Long userId, @Param("id") Long id);
    int deleteBatch(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}
