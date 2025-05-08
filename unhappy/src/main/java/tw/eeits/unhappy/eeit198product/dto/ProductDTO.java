package tw.eeits.unhappy.eeit198product.dto;

import java.math.BigDecimal;
import java.util.List;

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
    private Integer brandId;
    private String brandName;
    private Integer categoryId;
    private String categoryName;

    private String mainImageUrl; // 主圖 URL 欄位，用於列表顯示

    private Boolean isActive; // isActive 欄位

    private Integer stock; // stock 欄位

    private List<ProductMediaDto> images; // 這個通常只在詳細頁或編輯頁需要

    // Getter 和 Setter 會由 Lombok 的 @Data 自動生成，無需手動編寫
}
