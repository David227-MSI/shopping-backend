package tw.eeits.unhappy.ra.review.repository;

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

    // 檢查用戶是否已經評價過該商品
    boolean existsByUserIdAndOrderItemId(Integer userId, Integer orderItemId);

    // 取該商品的所有公開評論
    @Query(
        value = """
            SELECT r.* FROM product_review r
            JOIN order_item oi ON oi.id = r.order_item_id
            WHERE oi.product_id = :pid
                AND r.is_visible = 1
            """,
        countQuery = """
            SELECT COUNT(*) FROM product_review r
            JOIN order_item oi ON oi.id = r.order_item_id
            WHERE oi.product_id = :pid
                AND r.is_visible = 1
            """,
        nativeQuery = true)
    Page<ProductReview> findVisibleByProduct(@Param("pid") Integer pid, Pageable pageable);

    // 取該商品的有圖片公開評論
    @Query(value = """
        SELECT r.*
        FROM product_review r
        JOIN order_item oi ON oi.id = r.order_item_id
        WHERE oi.product_id = :pid
            AND r.is_visible = 1
            AND r.review_images IS NOT NULL
            AND r.review_images <> ''
        """,
        countQuery = """
            SELECT COUNT(*) FROM product_review r
            JOIN order_item oi ON oi.id = r.order_item_id
            WHERE oi.product_id = :pid
                AND r.is_visible = 1
                AND r.review_images IS NOT NULL
                AND r.review_images <> ''
            """,
        nativeQuery = true)
    Page<ProductReview> findVisibleWithImagesByProduct(@Param("pid") Integer pid, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProductReview r SET r.helpfulCount = r.helpfulCount + 1 WHERE r.id = :id")
    void incrementHelpfulCount(@Param("id") Integer id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProductReview r SET r.helpfulCount = r.helpfulCount - 1 WHERE r.id = :id AND r.helpfulCount > 0")
    void decrementHelpfulCount(@Param("id") Integer id);
}