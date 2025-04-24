package tw.eeits.unhappy.ra.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewTag;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductReviewRepositoryTest {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Test
    void testSaveAndFindProductReview() {
        ProductReview review = new ProductReview();
        review.setUserId(1001);        // 確保 user_id 中有此 ID
        review.setOrderItemId(1);      // 確保 order_item_id 中有此 ID
        review.setScoreQuality(5);
        review.setScoreDescription(5);
        review.setScoreDelivery(5);
        review.setIsVerifiedPurchase(true);
        review.setIsVisible(true);
        review.setHelpfulCount(0);
        review.setTagName(Set.of(ReviewTag.FAST, ReviewTag.QUALITY));

        ProductReview saved = productReviewRepository.save(review);
        assertNotNull(saved.getId());

        ProductReview fetched = productReviewRepository.findById(saved.getId()).orElseThrow();

        Set<String> expected = review.getTagName().stream().map(Enum::name).collect(Collectors.toSet());
        Set<String> actual = fetched.getTagName().stream().map(Enum::name).collect(Collectors.toSet());

        assertTrue(expected.containsAll(actual), "expected 不包含 actual");
        assertTrue(actual.containsAll(expected), "actual 不包含 expected");

        assertEquals(review.getUserId(), fetched.getUserId());
        assertEquals(review.getOrderItemId(), fetched.getOrderItemId());
    }
}
