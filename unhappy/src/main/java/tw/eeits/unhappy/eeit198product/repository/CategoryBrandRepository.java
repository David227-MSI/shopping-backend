package tw.eeits.unhappy.eeit198product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.eeits.unhappy.eeit198product.entity.Brand;
import tw.eeits.unhappy.eeit198product.entity.CategoryBrand;
import tw.eeits.unhappy.eeit198product.entity.CategoryBrandId;

public interface CategoryBrandRepository extends JpaRepository<CategoryBrand, CategoryBrandId> {

    @Query("SELECT cb.brand FROM CategoryBrand cb WHERE cb.category.id IN :categoryIds")
    List<Brand> findBrandsByCategoryIds(@Param("categoryIds") List<Integer> categoryIds);
}
