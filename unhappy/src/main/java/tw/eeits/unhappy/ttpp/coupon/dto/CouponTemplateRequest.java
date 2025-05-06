package tw.eeits.unhappy.ttpp.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;

@Data
public class CouponTemplateRequest {

    private Integer id;

    private Integer applicableId;

    @NotNull(message = "Applicable type 為必要資訊")
    private ApplicableType applicableType;

    @NotNull(message = "Min spend 為必要資訊")
    @Min(value = 0, message = "Minimum spend 必須 >= 0")
    private BigDecimal minSpend;

    @NotNull(message = "Discount type 為必要資訊")
    private DiscountType discountType;

    @NotNull(message = "Discount Value 為必要資訊")
    @DecimalMin(value = "0.01", message = "discountValue 必須 > 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.01", message = "當用百分比折扣時 Max discount 必須 > 0")
    private BigDecimal maxDiscount;

    @NotNull(message = "是否可轉移 為必要資訊")
    private Boolean tradeable;

    @NotNull(message = "開始時間 為必要資訊")
    private LocalDateTime startTime;

    @NotNull(message = "結束時間 為必要資訊")
    private LocalDateTime endTime;

    @NotNull(message = "媒體種類 為必要資訊")
    private MediaType mediaType;

    @NotNull(message = "媒體數據 為必要資訊")
    private MultipartFile mediaData;


    @AssertTrue(message = "使用百分比折扣時, 需要指定Max discount")
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

}
