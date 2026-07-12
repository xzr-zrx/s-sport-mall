package cn.bugstack.infrastructure.repository;

import cn.bugstack.domain.mall.adapter.repository.IMallRepository;
import cn.bugstack.domain.mall.model.entity.*;
import cn.bugstack.domain.mall.model.valobj.OrderStatusVO;
import cn.bugstack.infrastructure.dao.*;
import cn.bugstack.infrastructure.dao.po.CartItem;
import cn.bugstack.infrastructure.dao.po.MallOrder;
import cn.bugstack.infrastructure.dao.po.MallOrderItem;
import cn.bugstack.infrastructure.dao.po.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MallRepository implements IMallRepository {

    private final IProductDao productDao;
    private final ICartDao cartDao;
    private final IMallOrderDao orderDao;
    private final IPaymentDao paymentDao;
    private final IOutboxDao outboxDao;

    public MallRepository(IProductDao productDao, ICartDao cartDao, IMallOrderDao orderDao,
                          IPaymentDao paymentDao, IOutboxDao outboxDao) {
        this.productDao = productDao; this.cartDao = cartDao; this.orderDao = orderDao;
        this.paymentDao = paymentDao; this.outboxDao = outboxDao;
    }

    @Override
    public List<ProductEntity> listProducts(String keyword, String category, String sort, int offset, int limit) {
        return productDao.searchOnSale(keyword, category, sort, offset, limit).stream().map(this::toProduct).collect(Collectors.toList());
    }
    @Override public long countProducts(String keyword, String category) { return productDao.countOnSale(keyword, category); }
    @Override public ProductEntity getProduct(Long productId) { return toProduct(productDao.findById(productId)); }

    @Override
    public void saveProduct(ProductEntity entity) {
        Product po = toProductPo(entity); productDao.insert(po); entity.setId(po.getId());
    }
    @Override public void updateProduct(ProductEntity entity) { productDao.update(toProductPo(entity)); }
    @Override public boolean decreaseStock(Long productId, int quantity) { return productDao.decreaseStock(productId, quantity) == 1; }
    @Override public void increaseStock(Long productId, int quantity) { productDao.increaseStock(productId, quantity); }

    @Override public List<CartItemEntity> listCart(Long userId) { return cartDao.listByUser(userId).stream().map(this::toCart).collect(Collectors.toList()); }
    @Override public List<CartItemEntity> listCartByIds(Long userId, List<Long> itemIds) { return cartDao.listByIds(userId, itemIds).stream().map(this::toCart).collect(Collectors.toList()); }
    @Override public void upsertCartItem(Long userId, Long productId, int quantity) { cartDao.upsert(userId, productId, quantity); }
    @Override public void updateCartQuantity(Long userId, Long cartItemId, int quantity) { cartDao.updateQuantity(userId, cartItemId, quantity); }
    @Override public void deleteCartItem(Long userId, Long cartItemId) { cartDao.delete(userId, cartItemId); }
    @Override public void deleteCartItems(Long userId, List<Long> cartItemIds) { cartDao.deleteBatch(userId, cartItemIds); }

    @Override
    public void saveOrder(MallOrderEntity entity, List<MallOrderItemEntity> items) {
        MallOrder po = new MallOrder();
        po.setOrderNo(entity.getOrderNo()); po.setUserId(entity.getUserId()); po.setSource(entity.getSource());
        po.setRecipientName(entity.getRecipientName()); po.setRecipientPhone(entity.getRecipientPhone());
        po.setShippingAddress(entity.getShippingAddress()); po.setTotalAmount(entity.getTotalAmount());
        po.setPayAmount(entity.getPayAmount()); po.setStatus(entity.getStatus().name()); po.setExpireAt(entity.getExpireAt());
        orderDao.insertOrder(po);
        for (MallOrderItemEntity item : items) {
            MallOrderItem itemPo = new MallOrderItem();
            itemPo.setOrderNo(item.getOrderNo()); itemPo.setProductId(item.getProductId()); itemPo.setProductName(item.getProductName());
            itemPo.setProductCover(item.getProductCover()); itemPo.setUnitPrice(item.getUnitPrice()); itemPo.setQuantity(item.getQuantity());
            itemPo.setItemAmount(item.getItemAmount()); orderDao.insertItem(itemPo);
        }
    }

    @Override public MallOrderEntity getOrder(String orderNo) { return attachItems(orderDao.findByOrderNo(orderNo)); }
    @Override public MallOrderEntity getUserOrder(Long userId, String orderNo) { return attachItems(orderDao.findUserOrder(userId, orderNo)); }
    @Override public List<MallOrderEntity> listUserOrders(Long userId) { return orderDao.listByUser(userId).stream().map(this::attachItems).collect(Collectors.toList()); }
    @Override public boolean markOrderPaying(String orderNo) { return orderDao.markPaying(orderNo) > 0; }

    @Override
    public boolean markOrderPaid(String orderNo, String thirdPartyTradeNo) {
        int changed = orderDao.markPaid(orderNo, thirdPartyTradeNo);
        if (changed > 0) paymentDao.updateSuccess(orderNo, thirdPartyTradeNo);
        return changed > 0;
    }

    @Override
    public boolean closeOrder(String orderNo, String targetStatus) {
        int changed = orderDao.closeOrder(orderNo, targetStatus);
        if (changed > 0) paymentDao.updateClosed(orderNo);
        return changed > 0;
    }

    @Override public List<String> listNeedReconcileOrders() { return orderDao.listNeedReconcileOrders(); }
    @Override public List<String> listTimeoutOrders() { return orderDao.listTimeoutOrders(); }
    @Override public void savePayment(PaymentCreateResultEntity payment, BigDecimal amount) { paymentDao.upsertPayment(payment.getPaymentNo(), payment.getOrderNo(), amount); }
    @Override public void saveNotifyLog(String orderNo, String tradeNo, String requestHash, String result) {
        String safeResult = result == null ? "UNKNOWN" : result.substring(0, Math.min(result.length(), 255));
        paymentDao.insertNotifyLog(orderNo, tradeNo, requestHash, safeResult);
    }
    @Override public void saveOutbox(String aggregateId, String eventType, String payload) { outboxDao.insert(aggregateId, eventType, payload); }
    @Override public List<Long> listPendingOutboxIds() { return outboxDao.listPendingIds(); }
    @Override public void markOutboxPublished(Long id) { outboxDao.markPublished(id); }

    private ProductEntity toProduct(Product po) {
        if (po == null) return null;
        return ProductEntity.builder().id(po.getId()).name(po.getName()).category(po.getCategory()).description(po.getDescription()).coverUrl(po.getCoverUrl())
                .price(po.getPrice()).stock(po.getStock()).status(po.getStatus()).createTime(po.getCreateTime()).build();
    }

    private Product toProductPo(ProductEntity e) {
        Product po = new Product(); po.setId(e.getId()); po.setName(e.getName()); po.setCategory(e.getCategory()); po.setDescription(e.getDescription());
        po.setCoverUrl(e.getCoverUrl()); po.setPrice(e.getPrice()); po.setStock(e.getStock()); po.setStatus(e.getStatus()); return po;
    }

    private CartItemEntity toCart(CartItem po) {
        return CartItemEntity.builder().id(po.getId()).userId(po.getUserId()).productId(po.getProductId())
                .productName(po.getProductName()).productCover(po.getProductCover()).unitPrice(po.getUnitPrice())
                .quantity(po.getQuantity()).stock(po.getStock()).selected(po.getSelected()).build();
    }

    private long remainingSeconds(LocalDateTime expireAt) {
        if (expireAt == null) return 0L;
        return Math.max(Duration.between(LocalDateTime.now(), expireAt).getSeconds(), 0L);
    }

    private MallOrderEntity attachItems(MallOrder po) {
        if (po == null) return null;
        List<MallOrderItemEntity> items = orderDao.listItems(po.getOrderNo()).stream().map(item -> MallOrderItemEntity.builder()
                .id(item.getId()).orderNo(item.getOrderNo()).productId(item.getProductId()).productName(item.getProductName())
                .productCover(item.getProductCover()).unitPrice(item.getUnitPrice()).quantity(item.getQuantity())
                .itemAmount(item.getItemAmount()).build()).collect(Collectors.toList());
        return MallOrderEntity.builder().id(po.getId()).orderNo(po.getOrderNo()).userId(po.getUserId())
                .source(po.getSource()).recipientName(po.getRecipientName()).recipientPhone(po.getRecipientPhone())
                .shippingAddress(po.getShippingAddress()).totalAmount(po.getTotalAmount()).payAmount(po.getPayAmount())
                .status(OrderStatusVO.valueOf(po.getStatus()))
                .expireAt(po.getExpireAt()).remainingSeconds(remainingSeconds(po.getExpireAt()))
                .payTime(po.getPayTime()).closeTime(po.getCloseTime())
                .createTime(po.getCreateTime()).items(items).build();
    }
}
