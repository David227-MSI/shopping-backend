package tw.eeits.unhappy.ttpp.coupon.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "coupon_published")
@Data
public class CouponPublished {

    @Id
    @NotBlank(message = "id 不可為空值")
    @Size(min = 36, max = 36, message = "id 必須為 36 字元")
    @Column(name = "id", length = 36, columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @NotNull(message = "Coupon ID 不可為空值")
    @Column(name = "coupon_id", nullable = false)
    private Integer couponId;

    @NotNull(message = "User ID 不可為空值")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull(message = "isUsed 不可為空值")
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

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

}
