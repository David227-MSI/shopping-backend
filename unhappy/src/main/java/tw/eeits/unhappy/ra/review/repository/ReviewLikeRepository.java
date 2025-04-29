package tw.eeits.unhappy.ra.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Integer> {

    int countByProductReview_Id(Integer reviewId);

    boolean existsByProductReview_IdAndUserMember_Id(Integer reviewId, Integer userId);

    @Modifying
    @Query("""
        delete from ReviewLike rl
        where rl.productReview.id = :reviewId
            and rl.userMember.id = :userId
    """)
    void deleteByReviewIdAndUserId(@Param("reviewId") Integer reviewId,
                                    @Param("userId") Integer userId);

    @Query(value = """
        SELECT r.*
        FROM product_review r
        JOIN order_items oi ON oi.id = r.order_item_id
        JOIN review_like rl ON rl.review_id = r.id
        WHERE oi.product_id = :pid
            AND r.is_visible = 1
            AND r.review_images IS NOT NULL
            AND r.review_images <> ''
        """,
        countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM product_review r
            JOIN order_items oi ON oi.id = r.order_item_id
            JOIN review_like rl ON rl.review_id = r.id
            WHERE oi.product_id = :pid
                AND r.is_visible = 1
                AND r.review_images IS NOT NULL
                AND r.review_images <> ''
            """,
        nativeQuery = true)
    Page<ProductReview> findVisibleLikedWithImagesByProduct(@Param("pid") Integer productId, Pageable pageable);
}