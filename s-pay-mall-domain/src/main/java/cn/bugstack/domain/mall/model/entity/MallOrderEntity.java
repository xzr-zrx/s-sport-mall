package cn.bugstack.domain.mall.model.entity;

import cn.bugstack.domain.mall.model.valobj.OrderStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderEntity {
    private Long id;
    private String orderNo;
    private Long userId;
    private String source;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private OrderStatusVO status;
    private LocalDateTime expireAt;
    private Long remainingSeconds;
    private LocalDateTime payTime;
    private LocalDateTime closeTime;
    private LocalDateTime createTime;
    private List<MallOrderItemEntity> items;
}
