package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.MallOrder;
import cn.bugstack.infrastructure.dao.po.MallOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IMallOrderDao {
    int insertOrder(MallOrder order);
    int insertItem(MallOrderItem item);
    MallOrder findByOrderNo(@Param("orderNo") String orderNo);
    MallOrder findUserOrder(@Param("userId") Long userId, @Param("orderNo") String orderNo);
    List<MallOrder> listByUser(@Param("userId") Long userId);
    List<MallOrderItem> listItems(@Param("orderNo") String orderNo);
    int markPaying(@Param("orderNo") String orderNo);
    int markPaid(@Param("orderNo") String orderNo, @Param("tradeNo") String tradeNo);
    int closeOrder(@Param("orderNo") String orderNo, @Param("status") String status);
    List<String> listNeedReconcileOrders();
    List<String> listTimeoutOrders();
}
