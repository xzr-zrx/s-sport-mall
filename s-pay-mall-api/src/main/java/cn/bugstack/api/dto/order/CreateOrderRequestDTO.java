package cn.bugstack.api.dto.order;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreateOrderRequestDTO {

    /** CART or BUY_NOW. */
    @NotBlank
    @Pattern(regexp = "CART|BUY_NOW", message = "订单来源仅支持 CART 或 BUY_NOW")
    private String source;

    /** Used when source=CART. */
    private List<Long> cartItemIds;

    /** Used when source=BUY_NOW. */
    @Valid
    private List<OrderCreateItemDTO> items;

    @NotBlank
    @Size(max = 64)
    private String recipientName;

    @NotBlank
    @Pattern(regexp = "^[0-9+\\- ]{6,24}$", message = "联系电话格式不正确")
    private String recipientPhone;

    @NotBlank
    @Size(max = 255)
    private String shippingAddress;
}
