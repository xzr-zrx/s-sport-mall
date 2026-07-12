package cn.bugstack.api.dto.product;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    @NotBlank private String name;
    private String category = "SPORT_ACCESSORIES";
    private String description;
    private String coverUrl;
    @NotNull @DecimalMin("0.01") private BigDecimal price;
    @Min(0) private int stock;
    private String status = "ON_SALE";
}
