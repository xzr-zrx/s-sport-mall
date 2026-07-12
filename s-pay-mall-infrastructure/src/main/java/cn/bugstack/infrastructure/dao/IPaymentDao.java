package cn.bugstack.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface IPaymentDao {
    int upsertPayment(@Param("paymentNo") String paymentNo, @Param("orderNo") String orderNo, @Param("amount") BigDecimal amount);
    int updateSuccess(@Param("orderNo") String orderNo, @Param("tradeNo") String tradeNo);
    int updateClosed(@Param("orderNo") String orderNo);
    int insertNotifyLog(@Param("orderNo") String orderNo, @Param("tradeNo") String tradeNo,
                        @Param("requestHash") String requestHash, @Param("result") String result);
}
