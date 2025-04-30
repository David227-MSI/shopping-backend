// package tw.eeits.unhappy.ll._others;

// import java.util.List;
// import java.util.Set;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import tw.eeits.unhappy.eeit198product.entity.Product;



// @Repository
// public interface ProductRepository extends JpaRepository<Product, Integer> {

//     // @Query("SELECT p FROM Product p WHERE (:search IS NULL OR p.name LIKE %:search%) AND (:brandId IS NULL OR p.brand.id = :brandId) AND (:categoryIds IS NULL OR p.category.id IN :categoryIds)")
//     // List<Product> searchByCategories(@Param("search") String search,
//     //                                  @Param("categoryIds") List<Integer> categoryIds,
//     //                                  @Param("brandId") Integer brandId);

//     @Query(value = "SELECT TOP (:limit) * FROM product WHERE id <> :excludeId ORDER BY NEWID()", nativeQuery = true)
//     List<Product> findRandomProductsExcept(@Param("excludeId") Integer excludeId, @Param("limit") int limit);
//     @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% AND p.category IN :categories AND p.brand.id = :brandId")
    
//     List<Product> searchByCategories(@Param("keyword") String keyword, @Param("categories") List<Integer> categories, @Param("brandId") Integer brandId);




// } 