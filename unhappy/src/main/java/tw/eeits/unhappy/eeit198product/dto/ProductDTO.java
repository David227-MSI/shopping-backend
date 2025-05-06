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
    private String brandName;
    private String categoryName;

    private List<ProductMediaDto> images;

    // // Getter 和 Setter
    // public Integer getId() {
    //     return id;
    // }

    // public void setId(Integer id) {
    //     this.id = id;
    // }

    // public String getName() {
    //     return name;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public BigDecimal getUnitPrice() {
    //     return unitPrice;
    // }

    // public void setUnitPrice(BigDecimal unitPrice) {
    //     this.unitPrice = unitPrice;
    // }

    // public String getDescription() {
    //     return description;
    // }

    // public void setDescription(String description) {
    //     this.description = description;
    // }

    // public String getBrandName() {
    //     return brandName;
    // }

    // public void setBrandName(String brandName) {
    //     this.brandName = brandName;
    // }

    // public String getCategoryName() {
    //     return categoryName;
    // }

    // public void setCategoryName(String categoryName) {
    //     this.categoryName = categoryName;
    // }
}
