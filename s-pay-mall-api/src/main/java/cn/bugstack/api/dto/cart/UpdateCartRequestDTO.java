package cn.bugstack.api.dto.cart;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class UpdateCartRequestDTO { @Min(1) @Max(99) private int quantity; }
