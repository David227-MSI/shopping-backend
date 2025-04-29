package tw.eeits.unhappy.eeit198product.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class CategoryBrandId implements Serializable {

    private Integer categoryId;
    private Integer brandId;

    public CategoryBrandId() {}

    public CategoryBrandId(Integer categoryId, Integer brandId) {
        this.categoryId = categoryId;
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryBrandId)) return false;
        CategoryBrandId that = (CategoryBrandId) o;
        return Objects.equals(categoryId, that.categoryId) &&
               Objects.equals(brandId, that.brandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, brandId);
    }
}
