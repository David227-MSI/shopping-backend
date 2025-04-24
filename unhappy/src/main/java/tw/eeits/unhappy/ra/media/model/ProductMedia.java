package tw.eeits.unhappy.ra.media.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "product_media",
    // 同商品 + mediaOrder 唯一（防重複）
    uniqueConstraints = @UniqueConstraint(
        name = "uk_prod_media_order",
        columnNames = {"product_id","media_order"}
    ),
    // 幫查詢加入索引（加速抓主圖 / 排序）
    indexes = {
        @Index(name = "idx_prod_main", columnList = "product_id,is_main"),
        @Index(name = "idx_prod_order", columnList = "product_id,media_order")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "product_id", nullable = false)
    // @NotNull(message = "Product id 不可為空值")
    // private Product product;
    @NotNull(message = "Product id 不可為空值")
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @NotNull(message = "Media type 不可為空值")
    @Convert(converter = MediaTypeConverter.class)
    @Column(name="media_type", nullable=false, length=10)
    private MediaType mediaType;

    @NotNull(message = "Media url 不可為空值")
    @Size(max = 500)
    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @NotNull(message = "Alt text 不可為空值")
    @Size(max = 100)
    @Column(name = "alt_text", nullable = false, length = 100)
    private String altText = "";

    @NotNull(message = "Media order 不可為空值")
    @Column(name = "media_order", nullable = false)
    private Integer mediaOrder = 0;

    @NotNull(message = "Is main 不可為空值")
    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;

    @Column(name = "created_at")
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