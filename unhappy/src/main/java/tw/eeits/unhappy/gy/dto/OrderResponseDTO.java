package tw.eeits.unhappy.gy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
    private Integer orderId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String status;
    private String statusText;
    private String paymentStatus;
    private String paymentStatusText;
    private String paymentMethod;
    private String transactionNumber;
    private String couponDiscountType;
    private BigDecimal couponDiscountValue;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
}
