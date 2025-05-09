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
    private String mainImageUrl;
    private Boolean isActive;
    private Integer stock;
    private List<ProductMediaDto> images;

}
