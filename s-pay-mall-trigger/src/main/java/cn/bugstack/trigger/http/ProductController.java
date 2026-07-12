package cn.bugstack.trigger.http;

import cn.bugstack.api.response.Response;
import cn.bugstack.domain.mall.service.IMallService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IMallService mallService;

    public ProductController(IMallService mallService) {
        this.mallService = mallService;
    }

    @GetMapping
    public Response<?> list(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String category,
                            @RequestParam(defaultValue = "DEFAULT") String sort,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "12") int pageSize) {
        return Response.success(mallService.listProducts(keyword, category, sort, page, pageSize));
    }

    @GetMapping("/{id}")
    public Response<?> detail(@PathVariable Long id) {
        return Response.success(mallService.getProduct(id));
    }
}
