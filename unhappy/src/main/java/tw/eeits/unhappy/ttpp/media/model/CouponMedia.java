package tw.eeits.unhappy.ttpp.media.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;

@Entity
@Table(name = "coupon_media")
@Data
public class CouponMedia {
    
    // fk_coupon_media_coupon_template
    @ManyToOne
    @NotNull(message = "couponId 不可為空值")
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponTemplate couponTemplate;
    



    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "mediaData 不可為空值")
    @Column(name = "media_data", nullable = false)
    private byte[] mediaData;

    @NotNull(message = "mediaType 不可為空值")
    @Column(name = "media_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;




    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
