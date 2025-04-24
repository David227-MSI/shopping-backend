package tw.eeits.unhappy.gy.order.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.eeits.unhappy.gy.cart.repository.CartItemRepository;
import tw.eeits.unhappy.gy.domain.*;
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
import tw.eeits.unhappy.gy.repository.CouponPublishedRepository;
import tw.eeits.unhappy.gy.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CouponPublishedRepository couponPublishedRepository;

    // 提出重複計算方法
    private BigDecimal calculateFinalAmount(BigDecimal total, BigDecimal discount) {
        total = Optional.ofNullable(total).orElse(BigDecimal.ZERO);
        discount = Optional.ofNullable(discount).orElse(BigDecimal.ZERO);
        return total.subtract(discount);
    }

    // 建立DTO(提出方法)
    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(calculateFinalAmount(order.getTotalAmount(), order.getDiscountAmount()))
                .status(order.getStatus().name())
                .statusText(order.getStatus().getDisplayText())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentStatusText(order.getPaymentStatus().getDisplayText())
                .paymentMethod(order.getPaymentMethod())
                .transactionNumber(order.getTransactionNumber())
                .paidAt(order.getPaidAt())
                .createdAt(order.getCreatedAt())
                .build();
    }

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        // 找使用者
        UserMember user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId())); //範例 : 找不到使用者ID : userId

        // 抓取未結帳購物車資料
        List<CartItem> cartItemList = cartItemRepository.findByUserMember_IdAndCheckedOutFalse(user.getId());
        if (cartItemList.isEmpty()) {
            throw new EmptyCartException(dto.getUserId());
        }

        // 計算總金額
        BigDecimal totalAmount = cartItemList.stream()
                .map(item -> item.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 處理優惠券
        CouponPublished coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (dto.getCouponPublishedId() != null) {
            coupon = couponPublishedRepository.findByIdAndUserMember_Id(dto.getCouponPublishedId(), dto.getUserId())
                    .orElseThrow(() -> new InvalidCouponUsageException("無法使用此優惠券"));

            if (Boolean.TRUE.equals(coupon.getIsUsed())) {
                throw new InvalidCouponUsageException("此優惠券已使用");
            }

            CouponTemplate template = coupon.getCouponTemplate();
            if (template == null) {
                throw new InvalidCouponUsageException("優惠券模板資料錯誤");
            }

            // 確認是否達最低消費門檻
            BigDecimal minSpend = Optional.ofNullable(template.getMinSpend()).orElse(BigDecimal.ZERO);
            if (totalAmount.compareTo(minSpend) < 0) {
                throw new InvalidCouponUsageException("未達最低消費金額");
            }

            // 取得折扣金額
            discountAmount = Optional.ofNullable(template.getDiscountValue()).orElse(BigDecimal.ZERO);
            BigDecimal maxDiscount = Optional.ofNullable(template.getMaxDiscount()).orElse(discountAmount);

            // 若超過最大折扣則調整
            if (discountAmount.compareTo(maxDiscount) > 0) {
                discountAmount = maxDiscount;
            }

            // 優惠券標記為已使用
            coupon.setIsUsed(true);
            couponPublishedRepository.save(coupon);
        }

        // 建立訂單
        Order order = Order.builder()
                .userMember(user)
                .couponPublished(coupon)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .build();
        orderRepository.save(order);

        // 產生訂單明細
        List<OrderItem> orderItems = cartItemList.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .priceAtTheTime(cartItem.getProduct().getUnitPrice())
                        .productNameAtTheTime(cartItem.getProduct().getName())
                        .build())
                .collect(Collectors.toList());
        // 多筆資料用saveAll
        orderItemRepository.saveAll(orderItems);

        // 更新購物車狀態
        cartItemList.forEach(cartItem -> cartItem.setCheckedOut(true));
        cartItemRepository.saveAll(cartItemList);

        // 回傳DTO
        return convertToOrderResponseDTO(order);
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
