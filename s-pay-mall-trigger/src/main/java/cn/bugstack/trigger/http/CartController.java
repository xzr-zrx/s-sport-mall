package cn.bugstack.trigger.http;

import cn.bugstack.api.dto.cart.AddCartRequestDTO;
import cn.bugstack.api.dto.cart.UpdateCartRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.mall.service.IMallService;
import cn.bugstack.trigger.support.CurrentUser;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final IMallService mallService;
    public CartController(IMallService mallService) { this.mallService = mallService; }
    @GetMapping public Response<?> list() { return Response.success(mallService.listCart(CurrentUser.id())); }
    @PostMapping("/items") public Response<Void> add(@Valid @RequestBody AddCartRequestDTO req) { mallService.addCart(CurrentUser.id(), req.getProductId(), req.getQuantity()); return Response.success(); }
    @PutMapping("/items/{id}") public Response<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateCartRequestDTO req) { mallService.updateCart(CurrentUser.id(), id, req.getQuantity()); return Response.success(); }
    @DeleteMapping("/items/{id}") public Response<Void> delete(@PathVariable Long id) { mallService.deleteCart(CurrentUser.id(), id); return Response.success(); }
}
