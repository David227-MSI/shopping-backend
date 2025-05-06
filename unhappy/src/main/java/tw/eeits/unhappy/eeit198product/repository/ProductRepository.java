package tw.eeits.unhappy.eeit198product.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.eeits.unhappy.eeit198product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p "
         + "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) "
         + "  AND (:brandId IS NULL OR p.brand.id = :brandId) "
         + "  AND (:keyword IS NULL OR p.name LIKE %:keyword%)")
    List<Product> searchByCondition(
        @Param("categoryId") Integer categoryId,
        @Param("brandId")    Integer brandId,
        @Param("keyword")    String keyword
    );

    List<Product> findTop5ByIdNotOrderByCreatedAtDesc(Integer id);


    // LL
    // 月銷報表要用的
    List<Product> findByIdIn(Set<Integer> ids);


    // SubscriptionList
    @Query("SELECT p FROM Product p " +
       "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
       "  AND (:keyword IS NULL OR p.name LIKE %:keyword%) " +
       "  AND p.id IN :productIds")
    List<Product> findSubscribedProductsByCondition(
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword,
            @Param("productIds") List<Integer> productIds // 接收已追蹤的 Product IDs
    );





}
