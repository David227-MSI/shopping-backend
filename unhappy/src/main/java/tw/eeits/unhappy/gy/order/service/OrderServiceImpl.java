package tw.eeits.unhappy.gy.order.service;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.gy.cart.repository.CartItemRepository;
import tw.eeits.unhappy.gy.domain.CartItem;
import tw.eeits.unhappy.gy.domain.Order;
import tw.eeits.unhappy.gy.domain.OrderItem;
import tw.eeits.unhappy.gy.dto.OrderDetailResponseDTO;
import tw.eeits.unhappy.gy.dto.OrderItemResponseDTO;
import tw.eeits.unhappy.gy.dto.OrderRequestDTO;
import tw.eeits.unhappy.gy.dto.OrderResponseDTO;
import tw.eeits.unhappy.gy.enums.OrderStatus;
import tw.eeits.unhappy.gy.enums.PaymentStatus;
import tw.eeits.unhappy.gy.exception.EmptyCartException;
import tw.eeits.unhappy.gy.exception.InvalidCouponUsageException;
import tw.eeits.unhappy.gy.exception.OrderNotFoundException;
import tw.eeits.unhappy.gy.exception.UserNotFoundException;
import tw.eeits.unhappy.gy.order.repository.OrderItemRepository;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponPublishedRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserMemberRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CouponPublishedRepository couponPublishedRepository;

    // 訂單建立
    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        UserMember user = findUser(dto.getUserId());
        List<CartItem> cartItems = getCartItems(user.getId());
        BigDecimal totalAmount = calculateTotalAmount(cartItems);
        CouponUsageResult couponResult = applyCouponIfNeeded(dto, totalAmount);
        Order order = saveOrder(user, couponResult, totalAmount, dto);
        createOrderItems(order, cartItems);
        checkoutCartItems(cartItems);
        return convertToOrderResponseDTO(order);
    }

    // 小方法區

    // 查詢會員
    private UserMember findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 查詢購物車內商品
    private List<CartItem> getCartItems(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserMember_IdAndCheckedOutFalse(userId);
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(userId);
        }
        return cartItems;
    }

    // 計算總金額
    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 套用優惠券 ( 可能廢棄 )
    private CouponUsageResult applyCouponIfNeeded(OrderRequestDTO dto, BigDecimal totalAmount) {
        if (dto.getCouponPublishedId() == null) {
            return new CouponUsageResult(null, BigDecimal.ZERO);
        }

        CouponPublished coupon = couponPublishedRepository.findByIdAndUserMemberId(dto.getCouponPublishedId(), dto.getUserId())
                .orElseThrow(() -> new InvalidCouponUsageException("無法使用此優惠券"));

        if (Boolean.TRUE.equals(coupon.getIsUsed())) {
            throw new InvalidCouponUsageException("此優惠券已使用");
        }

        CouponTemplate template = Optional.ofNullable(coupon.getCouponTemplate())
                .orElseThrow(() -> new InvalidCouponUsageException("優惠券模板資料錯誤"));

        BigDecimal minSpend = Optional.ofNullable(template.getMinSpend()).orElse(BigDecimal.ZERO);
        if (totalAmount.compareTo(minSpend) < 0) {
            throw new InvalidCouponUsageException("未達最低消費金額");
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal discountValue = Optional.ofNullable(template.getDiscountValue()).orElse(BigDecimal.ZERO);
        BigDecimal maxDiscount = Optional.ofNullable(template.getMaxDiscount()).orElse(BigDecimal.ZERO);

        switch (template.getDiscountType()) {
            case VALUE -> discountAmount = discountValue;
            case PERCENTAGE -> {
                discountAmount = totalAmount.multiply(BigDecimal.valueOf(1).subtract(discountValue.divide(BigDecimal.valueOf(100))));
                // 若有最大折扣限制
                if (maxDiscount.compareTo(BigDecimal.ZERO) > 0 && discountAmount.compareTo(maxDiscount) > 0) {
                    discountAmount = maxDiscount;
                }
            }
            default -> discountAmount = BigDecimal.ZERO;
        }

        coupon.setIsUsed(true);
        couponPublishedRepository.save(coupon);

        return new CouponUsageResult(coupon, discountAmount);
    }

    // 儲存訂單(訂單)
    private Order saveOrder(UserMember user, CouponUsageResult couponResult, BigDecimal totalAmount, OrderRequestDTO dto) {
        Order order = Order.builder()
                .userMember(user)
                .couponPublished(couponResult.getCoupon())
                .totalAmount(totalAmount)
                .discountAmount(couponResult.getDiscountAmount())
                .status(OrderStatus.PENDING) // 暫時測試 ngrok
                .paymentStatus(PaymentStatus.UNPAID) // 暫時測試 ngrok
                .paymentMethod("綠界-信用卡一次付清")
                .recipientName(dto.getRecipientName())
                .recipientPhone(dto.getRecipientPhone())
                .recipientAddress(dto.getRecipientAddress())
                .build();
        return orderRepository.save(order);
    }

    // 儲存訂單明細 (商品)
    private void createOrderItems(Order order, List<CartItem> cartItems) {
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .priceAtTheTime(cartItem.getProduct().getUnitPrice())
                        .productNameAtTheTime(cartItem.getProduct().getName())
                        .build())
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);
    }

    // 結帳購物車商品 (標記為已結帳)
    private void checkoutCartItems(List<CartItem> cartItems) {
        cartItems.forEach(cartItem -> cartItemRepository.markCartItemCheckedOutById(cartItem.getId()));
    }

    // DTO轉換方法

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getTotalAmount().subtract(Optional.ofNullable(order.getDiscountAmount()).orElse(BigDecimal.ZERO)))
                .status(order.getStatus().name())
                .statusText(order.getStatus().getDisplayText())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentStatusText(order.getPaymentStatus().getDisplayText())
                .paymentMethod(order.getPaymentMethod())
                .transactionNumber(order.getTransactionNumber())
                .couponDiscountType(
                        order.getCouponPublished() != null &&
                                order.getCouponPublished().getCouponTemplate() != null
                                ? String.valueOf(order.getCouponPublished().getCouponTemplate().getDiscountType())
                                : null
                )
                .couponDiscountValue(
                        order.getCouponPublished() != null &&
                                order.getCouponPublished().getCouponTemplate() != null
                                ? order.getCouponPublished().getCouponTemplate().getDiscountValue()
                                : null
                )
                .paidAt(order.getPaidAt())
                .createdAt(order.getCreatedAt())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .build();
    }

    // 內部使用 處理套用優惠券結果的小類別

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class CouponUsageResult {
        private CouponPublished coupon;
        private BigDecimal discountAmount;
    }

    // 查某會員所有訂單
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUser(Integer userId) {

        UserMember user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Order> orders = orderRepository.findByUserMember_IdOrderByCreatedAtDesc(user.getId());

        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .toList();
    }

    // 查詢訂單明細
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getOrderDetail(Integer orderId) {

        // 從資料庫撈訂單
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));

        // 撈訂單明細
        List<OrderItemResponseDTO> itemDTOList = order.getOrderItems().stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .orderItemId(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getPriceAtTheTime())
                        .subtotal(item.getPriceAtTheTime().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        // 組合 OrderDetailResponseDTO
        return OrderDetailResponseDTO.builder()
                .order(convertToOrderResponseDTO(order))
                .orderDetails(itemDTOList)
                .build();
    }
}
