package tw.eeits.unhappy.eeit198product.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.eeits.unhappy.eeit198product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // ✅ 依分類、品牌、商品名稱模糊搜尋（商品列表頁使用）
    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand
        JOIN FETCH p.category
        WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:brandId IS NULL OR p.brand.id = :brandId)
          AND (:keyword IS NULL OR p.name LIKE %:keyword%)
    """)
    List<Product> searchByCondition(
        @Param("categoryId") Integer categoryId,
        @Param("brandId") Integer brandId,
        @Param("keyword") String keyword
    );

    // ✅ 推薦商品（排除指定商品，取最新前 5 筆）
    List<Product> findTop5ByIdNotOrderByCreatedAtDesc(Integer id);

    // ✅ 月銷報表用（批次查詢指定 ID 的商品）
    List<Product> findByIdIn(Set<Integer> ids);

    // ✅ 追蹤清單搜尋（根據分類、關鍵字與已追蹤 ID 清單）
    @Query("""
        SELECT p FROM Product p
        WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:keyword IS NULL OR p.name LIKE %:keyword%)
          AND p.id IN :productIds
    """)
    List<Product> findSubscribedProductsByCondition(
        @Param("categoryId") Integer categoryId,
        @Param("keyword") String keyword,
        @Param("productIds") List<Integer> productIds
    );

    // ✅ 全欄位搜尋（名稱、品牌、分類、父分類、屬性值，模糊比對）
    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN p.brand b
        JOIN p.category c
        LEFT JOIN c.parentCategory pc
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(pc.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR EXISTS (
               SELECT av FROM AttributeValue av
               WHERE av.product.id = p.id
                 AND LOWER(av.value) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
    """)
    List<Product> searchAllFields(@Param("keyword") String keyword);
}
