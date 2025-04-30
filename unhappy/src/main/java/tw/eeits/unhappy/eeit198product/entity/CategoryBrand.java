package tw.eeits.unhappy.eeit198product.entity;

import jakarta.persistence.*;
import tw.eeits.unhappy.ll.model.Brand;

@Entity
@Table(name = "category_brand")
public class CategoryBrand {

    @EmbeddedId
    private CategoryBrandId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("brandId")
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public CategoryBrand() {}

    public CategoryBrand(Category category, Brand brand) {
        this.category = category;
        this.brand = brand;
        this.id = new CategoryBrandId(category.getId(), brand.getId());
    }

    // getters and setters
}
