package tw.eeits.unhappy.ra.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewTag;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 確認 ProductReview Entity 能正確寫入／取出，
 * 以及 ReviewTag converter 是否正常運作。
 */
@SpringBootTest(properties = {
    // 給一條 Local-storage / Azurite 的開發字串
    "azure.storage.connection-string=UseDevelopmentStorage=true",
    "azure.storage.container=unit-test"
})
class ProductReviewRepositoryTest {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Test
    void testSaveAndFindProductReview() {
        /* ---------- Arrange ---------- */
        ProductReview review = new ProductReview();
        review.setUserId(1001);        // 確保 user_member table 有此 ID
        review.setOrderItemId(3);      // 確保 order_item table 有此 ID
        review.setScoreQuality(5);
        review.setScoreDescription(5);
        review.setScoreDelivery(5);
        review.setIsVerifiedPurchase(true);
        review.setIsVisible(true);
        review.setHelpfulCount(0);
        review.setTagName(Set.of(ReviewTag.FAST, ReviewTag.QUALITY));

        /* ---------- Act ---------- */
        ProductReview saved = productReviewRepository.save(review);
        assertNotNull(saved.getId(), "寫入後應該拿得到主鍵 ID");

        ProductReview fetched =
                productReviewRepository.findById(saved.getId()).orElseThrow();

        /* ---------- Assert ---------- */
        // 1. Tag 轉換正確
        Set<String> expected = review.getTagName()
                                     .stream()
                                     .map(Enum::name)
                                     .collect(Collectors.toSet());
        Set<String> actual   = fetched.getTagName()
                                     .stream()
                                     .map(Enum::name)
                                     .collect(Collectors.toSet());
        assertEquals(expected, actual);

        // 2. 其他欄位
        assertEquals(review.getUserId(),      fetched.getUserId());
        assertEquals(review.getOrderItemId(), fetched.getOrderItemId());
    }
}
