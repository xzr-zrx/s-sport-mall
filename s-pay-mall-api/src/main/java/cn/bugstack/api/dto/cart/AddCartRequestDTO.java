package cn.bugstack.api.dto.cart;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddCartRequestDTO {
    @NotNull private Long productId;
    @Min(1) @Max(99) private int quantity = 1;
}
