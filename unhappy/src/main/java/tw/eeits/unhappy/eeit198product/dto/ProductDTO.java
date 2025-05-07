package tw.eeits.unhappy.eeit198product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime; // 如果你需要傳遞時間相關的屬性

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.ra.media.dto.ProductMediaDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private Integer id;
    private String name;
    private BigDecimal unitPrice;
    private String description;

    // 【新增】用於接收前端傳來的品牌 ID 和分類 ID
    private Integer brandId;
    private Integer categoryId;

    // 這裡保留 brandName 和 categoryName，用於返回給前端顯示
    private String brandName;
    private String categoryName;

    // 【新增】用於接收前端傳來的上架狀態
    private Boolean isActive;

    // TODO: 如果你的商品還有其他屬性需要在 DTO 中傳遞，例如庫存、上下架時間等，也需要在這裡添加
    private Integer stock;
    // private LocalDateTime startTime;
    // private LocalDateTime endTime;


    private List<ProductMediaDto> images;

    // Lombok 的 @Data 會自動生成 Getter 和 Setter，所以你不需要手動寫 getBrandId(), getCategoryId(), getIsActive() 等方法。
}
