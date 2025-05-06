package tw.eeits.unhappy.ll.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.ll.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

	Optional<Brand> findByName(String brandName);

	// 月銷報表要用的
	List<Brand> findByIdIn(Set<Integer> ids);


	// Brown
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




    // SubscriptionList
    @Query("SELECT b FROM Brand b " +
       "WHERE (:keyword IS NULL OR b.name LIKE %:keyword%) " +
       "  AND b.id IN :brandIds")
    List<Brand> findSubscribedBrandsByCondition(
            @Param("keyword") String keyword,
            @Param("brandIds") List<Integer> brandIds
    );


}