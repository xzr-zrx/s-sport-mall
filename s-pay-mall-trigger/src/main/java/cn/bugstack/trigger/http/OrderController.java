package cn.bugstack.trigger.http;

import cn.bugstack.api.dto.order.CreateOrderRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.mall.model.entity.OrderCreateItemEntity;
import cn.bugstack.domain.mall.service.IMallService;
import cn.bugstack.trigger.support.CurrentUser;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final IMallService mallService;

    public OrderController(IMallService mallService) {
        this.mallService = mallService;
    }

    @PostMapping
    public Response<?> create(@Valid @RequestBody CreateOrderRequestDTO req) {
        List<OrderCreateItemEntity> items = req.getItems() == null
                ? Collections.emptyList()
                : req.getItems().stream()
                .map(item -> OrderCreateItemEntity.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return Response.success(mallService.createOrder(
                CurrentUser.id(),
                req.getSource(),
                req.getCartItemIds(),
                items,
                req.getRecipientName(),
                req.getRecipientPhone(),
                req.getShippingAddress()
        ));
    }

    @GetMapping
    public Response<?> list() {
        return Response.success(mallService.listOrders(CurrentUser.id()));
    }

    @GetMapping("/{orderNo}")
    public Response<?> detail(@PathVariable String orderNo) {
        return Response.success(mallService.getOrder(CurrentUser.id(), orderNo));
    }

    @PostMapping("/{orderNo}/cancel")
    public Response<Void> cancel(@PathVariable String orderNo) {
        mallService.cancelOrder(CurrentUser.id(), orderNo);
        return Response.success();
    }
}
