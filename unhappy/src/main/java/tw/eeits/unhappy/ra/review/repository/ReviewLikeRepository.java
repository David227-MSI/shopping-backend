package tw.eeits.unhappy.ra.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Integer> {
    
    // 這篇 review 總按讚數
    int countByProductReviewId(Integer reviewId);

    // 判斷使用者是否已經按過讚（避免重複）
    boolean existsByProductReviewIdAndUserId(Integer reviewId, Integer userId);

    void deleteByProductReviewIdAndUserId(Integer reviewId, Integer userId);

    // 查某商品的「有圖、公開」評論且被點讚
    @Query(value = """
        SELECT r.*
        FROM product_review r
        JOIN order_item    oi ON oi.id = r.order_item_id
        JOIN review_like  rl ON rl.review_id = r.id
        WHERE oi.product_id = :pid
            AND r.is_visible  = 1
            AND r.review_images IS NOT NULL
            AND r.review_images <> ''
        """,
        countQuery = """
            SELECT COUNT(DISTINCT r.id)           -- 用 DISTINCT 避免一篇被多個 Like 重複計數
            FROM product_review r
            JOIN order_item    oi ON oi.id = r.order_item_id
            JOIN review_like  rl ON rl.review_id = r.id
            WHERE oi.product_id = :pid
                AND r.is_visible  = 1
                AND r.review_images IS NOT NULL
                AND r.review_images <> ''
            """,
        nativeQuery = true)
    Page<ProductReview> findVisibleLikedWithImagesByProduct(@Param("pid") Integer productId, Pageable pageable);

}