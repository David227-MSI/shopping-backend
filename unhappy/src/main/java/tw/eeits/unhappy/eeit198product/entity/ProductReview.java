package tw.eeits.unhappy.eeit198product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_review")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    // 注意：這裡就只是普通 Integer 欄位，不要加 @ManyToOne
    private Integer orderItemId;

    private String reviewText;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reviewImages;

    private Integer scoreQuality;

    private Integer scoreDescription;

    private Integer scoreDelivery;

    private Boolean isVerifiedPurchase;

    private Boolean isVisible;

    private Integer helpfulCount;

    private String tagName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
