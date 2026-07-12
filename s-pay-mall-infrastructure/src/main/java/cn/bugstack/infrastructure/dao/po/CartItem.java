package cn.bugstack.infrastructure.dao.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productCover;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer stock;
    private Boolean selected;
}
