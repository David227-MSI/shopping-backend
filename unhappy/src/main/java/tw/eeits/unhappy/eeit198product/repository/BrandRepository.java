package tw.eeits.unhappy.eeit198product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.eeits.unhappy.eeit198product.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    /**
     * 透過中介表 category_brand，把某分類下的品牌撈出來
     */
    @Query(value = """
            SELECT b.* 
            FROM brand b
            JOIN category_brand cb ON b.id = cb.brand_id
            WHERE cb.category_id = :categoryId
            """, nativeQuery = true)
    List<Brand> findBrandsByCategoryId(@Param("categoryId") Integer categoryId);
}
