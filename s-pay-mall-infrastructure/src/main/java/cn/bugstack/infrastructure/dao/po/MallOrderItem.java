package cn.bugstack.infrastructure.dao.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MallOrderItem {
    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private String productCover;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal itemAmount;
}
