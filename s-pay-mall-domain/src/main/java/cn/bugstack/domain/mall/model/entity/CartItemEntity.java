package cn.bugstack.domain.mall.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemEntity {
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
