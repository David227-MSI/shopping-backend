package tw.eeits.unhappy.ra.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.eeits.unhappy.ra._fake.OrderItem;
import tw.eeits.unhappy.ra._fake.UserMember;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewLike;
import tw.eeits.unhappy.ra.review.model.ReviewTag;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewLikeRepositoryTest {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Test
    void testSaveAndFindReviewLike() {
        ProductReview review = new ProductReview();

        UserMember userReview = new UserMember();
        userReview.setId(1003);
        review.setUserMember(userReview);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(5);
        review.setOrderItem(orderItem);

        review.setScoreQuality(5);
        review.setScoreDescription(5);
        review.setScoreDelivery(5);
        review.setIsVerifiedPurchase(true);
        review.setIsVisible(true);
        review.setHelpfulCount(0);
        review.setTagName(Set.of(ReviewTag.FAST, ReviewTag.QUALITY));

        ProductReview savedReview = productReviewRepository.save(review);
        assertNotNull(savedReview.getId());

        ReviewLike like = new ReviewLike();
        like.setProductReview(savedReview);

        UserMember liker = new UserMember();
        liker.setId(1001);
        like.setUserMember(liker);

        ReviewLike savedLike = reviewLikeRepository.save(like);

        assertNotNull(savedLike.getId());

        ReviewLike fetched = reviewLikeRepository.findById(savedLike.getId()).orElseThrow();

        assertEquals(savedReview.getId(), fetched.getProductReview().getId());
        assertEquals(1001, fetched.getUserMember().getId());
    }
}
