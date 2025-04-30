package tw.eeits.unhappy.ra.review.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.gy.domain.OrderItem;
import tw.eeits.unhappy.ra.review.StringListJsonConverter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "userMember 不可為空值")
    private UserMember userMember;
    // @NotNull(message = "User id 不可為空值")
    // @Column(name = "user_id", nullable = false)
    // private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    @NotNull(message = "Order item 不可為空值")
    private OrderItem orderItem;
    // @NotNull(message = "orderItemId 不可為空值")
    // @Column(name = "order_item_id", nullable = false)
    // private Integer orderItemId;

    @Size(max = 1000)
    @Column(name = "review_text", length = 1000)
    private String reviewText;

    @Size(max = 1000)
    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "review_images", length = 1000)
    private List<String> reviewImages = List.of();  // ← String → List<String>

    @NotNull(message = "Score quality 不可為空值")
    @Min(1)
    @Max(5)
    @Column(name = "score_quality", nullable = false)
    private Integer scoreQuality;

    @NotNull(message = "Score description 不可為空值")
    @Min(1)
    @Max(5)
    @Column(name = "score_description", nullable = false)
    private Integer scoreDescription;

    @NotNull(message = "Score delivery 不可為空值")
    @Min(1)
    @Max(5)
    @Column(name = "score_delivery", nullable = false)
    private Integer scoreDelivery;

    @NotNull(message = "Is verified purchase 不可為空值")
    @Column(name = "is_verified_purchase", nullable = false)
    private Boolean isVerifiedPurchase = true;

    @NotNull(message = "Is visible 不可為空值")
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @NotNull(message = "Helpful count 不可為空值")
    @Column(name = "helpful_count", nullable = false)
    private Integer helpfulCount = 0;

    @Size(max = 3)
    @Convert(converter = ReviewTagConverter.class)
    @NotNull(message = "Tag name 不可為空值")
    @Column(name = "tag_name", nullable = false, length = 200)
    private Set<ReviewTag> tagName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "productReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}