package cn.bugstack.infrastructure.dao.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MallOrder {
    private Long id;
    private String orderNo;
    private Long userId;
    private String source;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;
    private LocalDateTime expireAt;
    private LocalDateTime payTime;
    private LocalDateTime closeTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
