package cn.bugstack.domain.mall.service;

import cn.bugstack.domain.mall.adapter.port.IPaymentPort;
import cn.bugstack.domain.mall.adapter.repository.IMallRepository;
import cn.bugstack.domain.mall.model.entity.*;
import cn.bugstack.domain.mall.model.valobj.OrderStatusVO;
import cn.bugstack.domain.mall.model.valobj.PaymentStatusVO;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MallService implements IMallService {

    private final IMallRepository repository;
    private final IPaymentPort paymentPort;

    public MallService(IMallRepository repository, IPaymentPort paymentPort) {
        this.repository = repository;
        this.paymentPort = paymentPort;
    }

    @Override
    public ProductPageEntity listProducts(String keyword, String category, String sort, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 48);
        int offset = (safePage - 1) * safePageSize;
        String safeSort = normalizeSort(sort);
        List<ProductEntity> records = repository.listProducts(trimToNull(keyword), trimToNull(category), safeSort, offset, safePageSize);
        long total = repository.countProducts(trimToNull(keyword), trimToNull(category));
        int pages = total == 0 ? 0 : (int) Math.ceil((double) total / safePageSize);
        return ProductPageEntity.builder().records(records).total(total).page(safePage).pageSize(safePageSize).pages(pages).build();
    }

    private String normalizeSort(String sort) {
        if (sort == null) return "DEFAULT";
        switch (sort.toUpperCase()) {
            case "PRICE_ASC":
            case "PRICE_DESC":
            case "NEWEST":
                return sort.toUpperCase();
            default:
                return "DEFAULT";
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    @Override
    public ProductEntity getProduct(Long productId) {
        ProductEntity product = repository.getProduct(productId);
        if (product == null) throw new AppException("PRODUCT_404", "商品不存在");
        return product;
    }

    @Override
    @Transactional
    public void saveProduct(ProductEntity product) {
        repository.saveProduct(product);
    }

    @Override
    @Transactional
    public void updateProduct(ProductEntity product) {
        repository.updateProduct(product);
    }

    @Override
    public List<CartItemEntity> listCart(Long userId) {
        return repository.listCart(userId);
    }

    @Override
    @Transactional
    public void addCart(Long userId, Long productId, int quantity) {
        validateQuantity(quantity);
        ProductEntity product = getProduct(productId);
        int existingQuantity = repository.listCart(userId).stream()
                .filter(item -> item.getProductId().equals(productId))
                .map(CartItemEntity::getQuantity)
                .findFirst()
                .orElse(0);
        int targetQuantity = existingQuantity + quantity;
        validateQuantity(targetQuantity);
        validatePurchasableProduct(product, targetQuantity);
        repository.upsertCartItem(userId, productId, quantity);
    }

    @Override
    @Transactional
    public void updateCart(Long userId, Long cartItemId, int quantity) {
        validateQuantity(quantity);
        CartItemEntity cartItem = repository.listCart(userId).stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException("CART_404", "购物车商品不存在"));
        ProductEntity product = getProduct(cartItem.getProductId());
        validatePurchasableProduct(product, quantity);
        repository.updateCartQuantity(userId, cartItemId, quantity);
    }

    @Override
    @Transactional
    public void deleteCart(Long userId, Long cartItemId) {
        repository.deleteCartItem(userId, cartItemId);
    }

    @Override
    @Transactional
    public MallOrderEntity createOrder(Long userId,
                                       String source,
                                       List<Long> cartItemIds,
                                       List<OrderCreateItemEntity> items,
                                       String recipientName,
                                       String recipientPhone,
                                       String shippingAddress) {
        String safeSource = normalizeOrderSource(source);
        validateDelivery(recipientName, recipientPhone, shippingAddress);

        List<OrderCreateItemEntity> requestedItems;
        List<Long> cartIdsToDelete = Collections.emptyList();
        if ("CART".equals(safeSource)) {
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                throw new AppException("ORDER_001", "请选择要结算的购物车商品");
            }
            List<Long> distinctIds = new ArrayList<>(new java.util.LinkedHashSet<>(cartItemIds));
            List<CartItemEntity> cartItems = repository.listCartByIds(userId, distinctIds);
            if (cartItems.size() != distinctIds.size()) {
                throw new AppException("ORDER_002", "购物车商品不存在或无权操作");
            }
            requestedItems = cartItems.stream()
                    .map(cart -> OrderCreateItemEntity.builder()
                            .productId(cart.getProductId())
                            .quantity(cart.getQuantity())
                            .build())
                    .collect(java.util.stream.Collectors.toList());
            cartIdsToDelete = distinctIds;
        } else {
            requestedItems = aggregateDirectItems(items);
            if (requestedItems.isEmpty()) {
                throw new AppException("ORDER_001", "请选择要购买的商品");
            }
        }

        String orderNo = nextOrderNo();
        BigDecimal total = BigDecimal.ZERO;
        List<MallOrderItemEntity> orderItems = new ArrayList<>();
        for (OrderCreateItemEntity requested : requestedItems) {
            validateQuantity(requested.getQuantity());
            ProductEntity product = getProduct(requested.getProductId());
            validatePurchasableProduct(product, requested.getQuantity());
            if (!repository.decreaseStock(product.getId(), requested.getQuantity())) {
                throw new AppException("ORDER_004", product.getName() + " 库存不足");
            }
            BigDecimal itemAmount = product.getPrice().multiply(BigDecimal.valueOf(requested.getQuantity()));
            total = total.add(itemAmount);
            orderItems.add(MallOrderItemEntity.builder()
                    .orderNo(orderNo)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productCover(product.getCoverUrl())
                    .unitPrice(product.getPrice())
                    .quantity(requested.getQuantity())
                    .itemAmount(itemAmount)
                    .build());
        }

        MallOrderEntity order = MallOrderEntity.builder()
                .orderNo(orderNo)
                .userId(userId)
                .source(safeSource)
                .recipientName(recipientName.trim())
                .recipientPhone(recipientPhone.trim())
                .shippingAddress(shippingAddress.trim())
                .totalAmount(total)
                .payAmount(total)
                .status(OrderStatusVO.PENDING_PAYMENT)
                .expireAt(LocalDateTime.now().plusMinutes(30))
                .remainingSeconds(30L * 60L)
                .createTime(LocalDateTime.now())
                .items(orderItems)
                .build();
        repository.saveOrder(order, orderItems);
        if (!cartIdsToDelete.isEmpty()) {
            repository.deleteCartItems(userId, cartIdsToDelete);
        }
        return order;
    }

    @Override
    public List<MallOrderEntity> listOrders(Long userId) {
        return repository.listUserOrders(userId);
    }

    @Override
    public MallOrderEntity getOrder(Long userId, String orderNo) {
        MallOrderEntity order = repository.getUserOrder(userId, orderNo);
        if (order == null) throw new AppException("ORDER_404", "订单不存在");
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        MallOrderEntity order = getOrder(userId, orderNo);
        if (order.getStatus() == OrderStatusVO.PAID) throw new AppException("ORDER_005", "已支付订单不能取消");
        if (order.getStatus() == OrderStatusVO.CANCELLED || order.getStatus() == OrderStatusVO.CLOSED) return;
        if (paymentPort.closePayment(orderNo) && repository.closeOrder(orderNo, OrderStatusVO.CANCELLED.name())) {
            restoreStock(order);
        } else {
            throw new AppException("ORDER_009", "订单取消失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public PaymentCreateResultEntity createPayment(Long userId, String orderNo) {
        MallOrderEntity order = getOrder(userId, orderNo);
        if (order.getStatus() == OrderStatusVO.PAID) throw new AppException("PAY_001", "订单已支付");
        if (order.getStatus() == OrderStatusVO.CLOSED || order.getStatus() == OrderStatusVO.CANCELLED) {
            throw new AppException("PAY_002", "订单已关闭");
        }
        if (order.getExpireAt().isBefore(LocalDateTime.now())) {
            closeTimeoutOrder(orderNo);
            throw new AppException("PAY_003", "订单已超时关闭");
        }
        PaymentCreateResultEntity result = paymentPort.createPagePayment(order);
        repository.savePayment(result, order.getPayAmount());
        repository.markOrderPaying(orderNo);
        return result;
    }

    @Override
    public boolean verifyNotify(String orderNo, BigDecimal totalAmount) {
        MallOrderEntity order = repository.getOrder(orderNo);
        return order != null && totalAmount != null && order.getPayAmount().compareTo(totalAmount) == 0;
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(String orderNo, String tradeNo, String requestHash) {
        MallOrderEntity order = repository.getOrder(orderNo);
        if (order == null) throw new AppException("PAY_404", "支付订单不存在");
        boolean changed = repository.markOrderPaid(orderNo, tradeNo);
        if (!changed) {
            MallOrderEntity latest = repository.getOrder(orderNo);
            if (latest == null || latest.getStatus() != OrderStatusVO.PAID) {
                repository.saveNotifyLog(orderNo, tradeNo, requestHash, "REJECTED_ORDER_STATUS");
                throw new AppException("PAY_004", "订单当前状态不允许支付");
            }
        }
        repository.saveNotifyLog(orderNo, tradeNo, requestHash, changed ? "SUCCESS" : "IDEMPOTENT");
        if (changed) {
            repository.saveOutbox(orderNo, "ORDER_PAID", JSON.toJSONString(java.util.Map.of("orderNo", orderNo, "tradeNo", tradeNo)));
        }
    }

    @Override
    public void recordNotifyFailure(String orderNo, String tradeNo, String requestHash, String reason) {
        repository.saveNotifyLog(orderNo, tradeNo, requestHash, "FAILED:" + reason);
    }

    @Override
    @Transactional
    public void reconcileOrder(String orderNo) {
        MallOrderEntity order = repository.getOrder(orderNo);
        if (order == null || order.getStatus() == OrderStatusVO.PAID) return;
        PaymentQueryResultEntity result = paymentPort.queryPayment(orderNo);
        if (result.getStatus() == PaymentStatusVO.SUCCESS) {
            handlePaymentSuccess(orderNo, result.getThirdPartyTradeNo(), "ACTIVE_QUERY");
        } else if (result.getStatus() == PaymentStatusVO.CLOSED) {
            if (repository.closeOrder(orderNo, OrderStatusVO.CLOSED.name())) restoreStock(order);
        }
    }

    @Override
    @Transactional
    public void closeTimeoutOrder(String orderNo) {
        MallOrderEntity order = repository.getOrder(orderNo);
        if (order == null || order.getStatus() == OrderStatusVO.PAID || order.getStatus() == OrderStatusVO.CLOSED
                || order.getStatus() == OrderStatusVO.CANCELLED) return;
        PaymentQueryResultEntity result = paymentPort.queryPayment(orderNo);
        if (result.getStatus() == PaymentStatusVO.SUCCESS) {
            handlePaymentSuccess(orderNo, result.getThirdPartyTradeNo(), "TIMEOUT_QUERY");
            return;
        }
        if (paymentPort.closePayment(orderNo) && repository.closeOrder(orderNo, OrderStatusVO.CLOSED.name())) {
            restoreStock(order);
        }
    }

    @Override
    public List<String> listNeedReconcileOrders() {
        return repository.listNeedReconcileOrders();
    }

    @Override
    public List<String> listTimeoutOrders() {
        return repository.listTimeoutOrders();
    }

    @Override
    @Transactional
    public void publishOutbox() {
        for (Long id : repository.listPendingOutboxIds()) {
            log.info("outbox event published id={}", id);
            repository.markOutboxPublished(id);
        }
    }

    private List<OrderCreateItemEntity> aggregateDirectItems(List<OrderCreateItemEntity> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderCreateItemEntity item : items) {
            if (item == null || item.getProductId() == null || item.getQuantity() == null) {
                throw new AppException("ORDER_006", "购买商品参数不完整");
            }
            validateQuantity(item.getQuantity());
            int merged = quantities.getOrDefault(item.getProductId(), 0) + item.getQuantity();
            validateQuantity(merged);
            quantities.put(item.getProductId(), merged);
        }
        List<OrderCreateItemEntity> result = new ArrayList<>();
        quantities.forEach((productId, quantity) -> result.add(OrderCreateItemEntity.builder()
                .productId(productId)
                .quantity(quantity)
                .build()));
        return result;
    }

    private String normalizeOrderSource(String source) {
        if (source == null) throw new AppException("ORDER_007", "订单来源不能为空");
        String value = source.trim().toUpperCase();
        if (!"CART".equals(value) && !"BUY_NOW".equals(value)) {
            throw new AppException("ORDER_007", "不支持的订单来源");
        }
        return value;
    }

    private void validateDelivery(String recipientName, String recipientPhone, String shippingAddress) {
        if (trimToNull(recipientName) == null || trimToNull(recipientPhone) == null || trimToNull(shippingAddress) == null) {
            throw new AppException("ORDER_008", "请填写完整的收货信息");
        }
        if (recipientName.trim().length() > 64 || recipientPhone.trim().length() > 24 || shippingAddress.trim().length() > 255) {
            throw new AppException("ORDER_008", "收货信息长度不符合要求");
        }
    }

    private void validatePurchasableProduct(ProductEntity product, int quantity) {
        if (!"ON_SALE".equals(product.getStatus())) throw new AppException("PRODUCT_001", "商品已下架");
        if (product.getStock() < quantity) throw new AppException("PRODUCT_002", "商品库存不足");
    }

    private void restoreStock(MallOrderEntity order) {
        if (order.getItems() == null) return;
        order.getItems().forEach(item -> repository.increaseStock(item.getProductId(), item.getQuantity()));
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1 || quantity > 99) throw new AppException("CART_001", "商品数量必须在 1 到 99 之间");
    }

    private String nextOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(100000, 999999);
    }
}
