// package tw.eeits.unhappy.eeit198product.repository;

// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.CrudRepository;
// import org.springframework.data.repository.query.Param;
// import tw.eeits.unhappy.eeit198product.entity.ProductReview;

// import java.util.List;

// public interface ProductReviewRepository extends CrudRepository<ProductReview, Integer> {

//     @Query(value = "SELECT pr.* FROM product_review pr " +
//                    "WHERE pr.order_item_id IN (" +
//                    "SELECT oi.id FROM order_items oi WHERE oi.product_id = :productId" +
//                    ") AND pr.is_visible = 1", nativeQuery = true)
//     List<ProductReview> findReviewsByProductId(@Param("productId") Integer productId);
// }
