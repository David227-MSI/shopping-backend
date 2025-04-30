package tw.eeits.unhappy.ra.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.ra.review.model.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    @Query("""
        SELECT r
        FROM ProductReview r
        WHERE r.orderItem.product.id = :productId
            AND r.isVisible = :isVisible
        """)
    Page<ProductReview> findByProductIdAndIsVisible(
        @Param("productId") Integer productId,
        @Param("isVisible") Boolean isVisible,
        Pageable pageable
    );

    @Query("""
        SELECT r
        FROM ProductReview r
        WHERE r.orderItem.product.id = :productId
            AND r.isVisible = :isVisible
            AND r.reviewImages IS NOT NULL
        """)
    Page<ProductReview> findByProductIdAndIsVisibleAndReviewImagesIsNotNull(
        @Param("productId") Integer productId,
        @Param("isVisible") Boolean isVisible,
        Pageable pageable
    );

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProductReview r SET r.helpfulCount = r.helpfulCount + 1 WHERE r.id = :id")
    void incrementHelpfulCount(@Param("id") Integer id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProductReview r SET r.helpfulCount = r.helpfulCount - 1 WHERE r.id = :id AND r.helpfulCount > 0")
    void decrementHelpfulCount(@Param("id") Integer id);

    @Query("""
        SELECT COUNT(r) > 0
        FROM ProductReview r
        WHERE r.userMember.id = :userId
            AND r.orderItem.id = :orderItemId
        """)
    boolean existsByUserIdAndOrderItemId(
        @Param("userId") Integer userId,
        @Param("orderItemId") Integer orderItemId
    );



    // Product
    @Query(value = "SELECT pr.* FROM product_review pr " +
                   "WHERE pr.order_item_id IN (" +
                   "SELECT oi.id FROM order_items oi WHERE oi.product_id = :productId" +
                   ") AND pr.is_visible = 1", nativeQuery = true)
    List<ProductReview> findReviewsByProductId(@Param("productId") Integer productId);


}
