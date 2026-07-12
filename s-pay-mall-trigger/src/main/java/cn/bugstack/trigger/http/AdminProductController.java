package cn.bugstack.trigger.http;

import cn.bugstack.api.dto.product.ProductRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.mall.model.entity.ProductEntity;
import cn.bugstack.domain.mall.service.IMallService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {
    private final IMallService mallService;
    public AdminProductController(IMallService mallService) { this.mallService = mallService; }

    @PostMapping public Response<?> create(@Valid @RequestBody ProductRequestDTO req) {
        ProductEntity product = toEntity(null, req); mallService.saveProduct(product); return Response.success(product);
    }
    @PutMapping("/{id}") public Response<?> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO req) {
        ProductEntity product = toEntity(id, req); mallService.updateProduct(product); return Response.success(product);
    }
    private ProductEntity toEntity(Long id, ProductRequestDTO r) {
        return ProductEntity.builder().id(id).name(r.getName()).category(r.getCategory()).description(r.getDescription()).coverUrl(r.getCoverUrl())
                .price(r.getPrice()).stock(r.getStock()).status(r.getStatus()).build();
    }
}
