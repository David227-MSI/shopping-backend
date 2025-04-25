package tw.eeits.unhappy.ra.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ra.review.model.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "azure.storage.connection-string=UseDevelopmentStorage=true",
    "azure.storage.container=unit-test"
})
class ReviewLikeRepositoryTest {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Test
    void testSaveAndFindReviewLike() {

        /* ---------- 1. 先建立一筆全新的 ProductReview ---------- */
        ProductReview review = new ProductReview();
        review.setUserId(1003);        // 確保 user_member 有此人
        review.setOrderItemId(5);      // 確保 order_items 有此筆
        review.setScoreQuality(5);
        review.setScoreDescription(5);
        review.setScoreDelivery(5);
        review.setIsVerifiedPurchase(true);
        review.setIsVisible(true);
        review.setHelpfulCount(0);
        review.setTagName(Set.of(ReviewTag.FAST, ReviewTag.QUALITY));

        ProductReview savedReview = productReviewRepository.save(review);
        assertNotNull(savedReview.getId(), "評論應成功寫入 DB");

        /* ---------- 2. 建立 ReviewLike，指向剛才那筆評論 ---------- */
        ReviewLike like = new ReviewLike();
        like.setProductReview(savedReview);  // 假設 ReviewLike 與 ProductReview 為 @ManyToOne
        like.setUserId(1001);                // 同一位使用者點讚
        ReviewLike savedLike = reviewLikeRepository.save(like);

        assertNotNull(savedLike.getId(), "ReviewLike 應成功寫入 DB");

        /* ---------- 3. 查詢並驗證 ---------- */
        ReviewLike fetched = reviewLikeRepository.findById(savedLike.getId())
                                                .orElseThrow();

        assertEquals(savedReview.getId(), fetched.getProductReview().getId(), "ReviewLike 指向的 Review ID 應一致");
        assertEquals(1001, fetched.getUserId(), "User ID 應一致");
    }
}
