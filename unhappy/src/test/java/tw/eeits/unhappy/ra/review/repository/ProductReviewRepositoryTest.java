package tw.eeits.unhappy.ra.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.eeits.unhappy.ra._fake.OrderItem;
import tw.eeits.unhappy.ra._fake.UserMember;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewTag;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductReviewRepositoryTest {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Test
    void testSaveAndFindProductReview() {
        ProductReview review = new ProductReview();

        UserMember user = new UserMember();
        user.setId(1001);
        review.setUserMember(user);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(3);
        review.setOrderItem(orderItem);

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
        assertEquals(expected, actual);

        assertEquals(review.getUserMember().getId(), fetched.getUserMember().getId());
        assertEquals(review.getOrderItem().getId(), fetched.getOrderItem().getId());
    }
}
