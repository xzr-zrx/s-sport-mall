package cn.bugstack.domain.mall.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String coverUrl;
    private BigDecimal price;
    private Integer stock;
    private String status;
    private LocalDateTime createTime;
}
