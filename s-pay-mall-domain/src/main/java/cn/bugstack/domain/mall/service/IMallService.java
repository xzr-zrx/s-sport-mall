package cn.bugstack.domain.mall.service;

import cn.bugstack.domain.mall.model.entity.CartItemEntity;
import cn.bugstack.domain.mall.model.entity.MallOrderEntity;
import cn.bugstack.domain.mall.model.entity.OrderCreateItemEntity;
import cn.bugstack.domain.mall.model.entity.PaymentCreateResultEntity;
import cn.bugstack.domain.mall.model.entity.ProductEntity;
import cn.bugstack.domain.mall.model.entity.ProductPageEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IMallService {
    ProductPageEntity listProducts(String keyword, String category, String sort, int page, int pageSize);
    ProductEntity getProduct(Long productId);
    void saveProduct(ProductEntity product);
    void updateProduct(ProductEntity product);

    List<CartItemEntity> listCart(Long userId);
    void addCart(Long userId, Long productId, int quantity);
    void updateCart(Long userId, Long cartItemId, int quantity);
    void deleteCart(Long userId, Long cartItemId);

    MallOrderEntity createOrder(Long userId,
                                String source,
                                List<Long> cartItemIds,
                                List<OrderCreateItemEntity> items,
                                String recipientName,
                                String recipientPhone,
                                String shippingAddress);
    List<MallOrderEntity> listOrders(Long userId);
    MallOrderEntity getOrder(Long userId, String orderNo);
    void cancelOrder(Long userId, String orderNo);

    PaymentCreateResultEntity createPayment(Long userId, String orderNo);
    boolean verifyNotify(String orderNo, BigDecimal totalAmount);
    void handlePaymentSuccess(String orderNo, String tradeNo, String requestHash);
    void recordNotifyFailure(String orderNo, String tradeNo, String requestHash, String reason);
    void reconcileOrder(String orderNo);
    void closeTimeoutOrder(String orderNo);
    List<String> listNeedReconcileOrders();
    List<String> listTimeoutOrders();
    void publishOutbox();
}
