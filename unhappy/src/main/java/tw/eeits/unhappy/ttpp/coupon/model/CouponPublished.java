package tw.eeits.unhappy.ttpp.coupon.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon_published")
public class CouponPublished {


    // fk_coupon_published_coupon_template
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "coupon_id", nullable = false)
    @NotNull(message = "couponTemplate 不可為空值")
    private CouponTemplate couponTemplate;



    // fk_coupon_published_user
    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull(message = "userMember 不可為空值")
    @JoinColumn(name = "user_id", nullable = false)
    private UserMember userMember;
    // |||                |||
    // vvv to be replaced vvv
    // @NotNull(message = "userId 不可為空值")
    // @Column(name = "user_id", nullable = false)
    // private Integer userId; // fk


    @Id
    @NotBlank(message = "id 不可為空值")
    @Size(min = 36, max = 36, message = "id 必須為 36 字元")
    @Column(name = "id", length = 36, columnDefinition = "CHAR(36)")
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotNull(message = "isUsed 不可為空值")
    @Column(name = "is_used", nullable = false)
    @Builder.Default
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
