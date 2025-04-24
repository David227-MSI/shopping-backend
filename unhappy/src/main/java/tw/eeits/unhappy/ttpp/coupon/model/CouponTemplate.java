package tw.eeits.unhappy.ttpp.coupon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;
import tw.eeits.unhappy.ttpp.media.model.CouponMedia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon_template")
public class CouponTemplate {

    // mapped: fk_coupon_published_coupon_template
    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponPublished> couponPublished = new ArrayList<>();

    // mapped: fk_coupon_media_coupon_template
    @Builder.Default
    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponMedia> couponMedia = new ArrayList<>();



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
    @Builder.Default
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
    @Builder.Default
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

    @AssertTrue(message = "當applicableType為ALL時, 不可指定applicableId")
    private boolean isApplicableIdValid() {
        if (applicableType == ApplicableType.ALL) {
            return applicableId == null;
        } else {
            return true;
        }
    }


    // mapped: fk_coupon_published_coupon_template
    public void addCouponPublished(CouponPublished published) {
        couponPublished.add(published);
        published.setCouponTemplate(this);
    }
    public void removeCouponPublished(CouponPublished published) {
        couponPublished.remove(published);
        published.setCouponTemplate(null);
    }

    // mapped: fk_coupon_media_coupon_template
    public void addCouponMedia(CouponMedia media) {
        couponMedia.add(media);
        media.setCouponTemplate(this);
    }
    public void removeCouponMedia(CouponMedia media) {
        couponMedia.remove(media);
        media.setCouponTemplate(null);
    }

}
