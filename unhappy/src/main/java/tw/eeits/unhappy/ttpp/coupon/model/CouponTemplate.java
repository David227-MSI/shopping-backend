package tw.eeits.unhappy.ttpp.coupon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;

@Entity
@Table(name = "coupon_template")
@Data
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "applicable_id")
    private Integer applicableId;

    @NotNull(message = "Applicable type 不可為空值")
    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_type", nullable = false, length = 10)
    private ApplicableType applicableType;

    @NotNull(message = "Min spend 不可為空值")
    @Min(value = 0, message = "Minimum spend 必須 >= 0")
    @Column(name = "min_spend", nullable = false, precision = 15, scale = 2)
    private BigDecimal minSpend = BigDecimal.ZERO;

    @NotNull(message = "Discount type 不可為空值")
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 10)
    private DiscountType discountType;

    @NotNull(message = "Discount Value 不可為空值")
    @DecimalMin(value = "0.01", message = "discountValue 必須 > 0")
    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @DecimalMin(value = "0.01", message = "當用百分比折扣時 Max discount 必須 > 0")
    @Column(name = "max_discount", precision = 15, scale = 2)
    private BigDecimal maxDiscount;

    @NotNull(message = "Tradeable 不可為空值")
    @Column(name = "tradeable", nullable = false)
    private Boolean tradeable = false;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 自訂驗證 max_discount 的邏輯
    @AssertTrue(message = "Max discount must be non-null and greater than 0 for PERCENTAGE discount type, or null for VALUE discount type")
    private boolean isMaxDiscountValid() {
        if (discountType == DiscountType.PERCENTAGE) {
            return maxDiscount != null && maxDiscount.compareTo(BigDecimal.ZERO) > 0;
        } else {
            return maxDiscount == null;
        }
    }
}
