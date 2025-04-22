package tw.eeits.unhappy.ra.review.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_review", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "order_item_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Size(max = 1000)
    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "review_images")
    private String reviewImages;

    @NotNull(message = "Score quality 不可為空值")
    @Column(name = "score_quality", nullable = false)
    private Integer scoreQuality;

    @NotNull(message = "Score description 不可為空值")
    @Column(name = "score_description", nullable = false)
    private Integer scoreDescription;

    @NotNull(message = "Score delivery 不可為空值")
    @Column(name = "score_delivery", nullable = false)
    private Integer scoreDelivery;

    @NotNull(message = "Is verified purchase 不可為空值")
    @Column(name = "is_verified_purchase", nullable = false)
    private Boolean isVerifiedPurchase;

    @NotNull(message = "is visible 不可為空值")
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;

    @Column(name = "helpful_count")
    private Integer helpfulCount;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}