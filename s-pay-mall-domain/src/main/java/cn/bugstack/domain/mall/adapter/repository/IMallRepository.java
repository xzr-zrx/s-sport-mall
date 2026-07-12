package cn.bugstack.domain.mall.adapter.repository;

import cn.bugstack.domain.mall.model.entity.CartItemEntity;
import cn.bugstack.domain.mall.model.entity.MallOrderEntity;
import cn.bugstack.domain.mall.model.entity.MallOrderItemEntity;
import cn.bugstack.domain.mall.model.entity.PaymentCreateResultEntity;
import cn.bugstack.domain.mall.model.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IMallRepository {
    List<ProductEntity> listProducts(String keyword, String category, String sort, int offset, int limit);
    long countProducts(String keyword, String category);
    ProductEntity getProduct(Long productId);
    void saveProduct(ProductEntity product);
    void updateProduct(ProductEntity product);
    boolean decreaseStock(Long productId, int quantity);
    void increaseStock(Long productId, int quantity);

    List<CartItemEntity> listCart(Long userId);
    List<CartItemEntity> listCartByIds(Long userId, List<Long> itemIds);
    void upsertCartItem(Long userId, Long productId, int quantity);
    void updateCartQuantity(Long userId, Long cartItemId, int quantity);
    void deleteCartItem(Long userId, Long cartItemId);
    void deleteCartItems(Long userId, List<Long> cartItemIds);

    void saveOrder(MallOrderEntity order, List<MallOrderItemEntity> items);
    MallOrderEntity getOrder(String orderNo);
    MallOrderEntity getUserOrder(Long userId, String orderNo);
    List<MallOrderEntity> listUserOrders(Long userId);
    boolean markOrderPaying(String orderNo);
    boolean markOrderPaid(String orderNo, String thirdPartyTradeNo);
    boolean closeOrder(String orderNo, String targetStatus);
    List<String> listNeedReconcileOrders();
    List<String> listTimeoutOrders();
    void savePayment(PaymentCreateResultEntity payment, BigDecimal amount);
    void saveNotifyLog(String orderNo, String tradeNo, String requestHash, String result);
    void saveOutbox(String aggregateId, String eventType, String payload);
    List<Long> listPendingOutboxIds();
    void markOutboxPublished(Long id);
}
