package cn.bugstack.api.dto.order;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderCreateItemDTO {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer quantity;
}
