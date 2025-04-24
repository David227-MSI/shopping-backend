package tw.eeits.unhappy.ttpp.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;

@Data
public class CouponTemplateRequest {

    private Integer id;

    private Integer applicableId;

    @NotNull(message = "Applicable type 不可為空值")
    private ApplicableType applicableType;

    @NotNull(message = "Min spend 不可為空值")
    @Min(value = 0, message = "Minimum spend 必須 >= 0")
    private BigDecimal minSpend;

    @NotNull(message = "Discount type 不可為空值")
    private DiscountType discountType;

    @NotNull(message = "Discount Value 不可為空值")
    @DecimalMin(value = "0.01", message = "discountValue 必須 > 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.01", message = "當用百分比折扣時 Max discount 必須 > 0")
    private BigDecimal maxDiscount;

    @NotNull(message = "Tradeable 不可為空值")
    private Boolean tradeable;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @AssertTrue(message = "Max discount must be non-null and greater than 0 for PERCENTAGE discount type, or null for VALUE discount type")
    private boolean isMaxDiscountValid() {
        if (discountType == DiscountType.PERCENTAGE) {
            return maxDiscount != null && maxDiscount.compareTo(BigDecimal.ZERO) > 0;
        } else {
            return maxDiscount == null;
        }
    }

    @AssertTrue(message = "當applicableType為ALL時, 不可指定applicableId")
    private boolean isApplicableIdValid1() {
        if (applicableType == ApplicableType.ALL) {
            return applicableId == null;
        } else {
            return true;
        }
    }

    @AssertTrue(message = "需要指定applicableId")
    private boolean isApplicableIdValid2() {
        return (
            applicableType == ApplicableType.BRAND || 
            applicableType == ApplicableType.PRODUCT
        ) && 
            applicableId != null;
    }

}
