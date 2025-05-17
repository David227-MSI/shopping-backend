package tw.eeits.unhappy.ra.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.model.ReviewTag;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.repository.ProductReviewRepository;
import tw.eeits.unhappy.ra.review.repository.ReviewLikeRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ProductReviewRepository reviewRepo;

    @Mock
    private ReviewLikeRepository likeRepo;

    @InjectMocks
    private ReviewService reviewService;

    @Nested
    @DisplayName("toggleLike()")
    class ToggleLike {

        @Test
        @DisplayName("toggleLike() - 已點讚，應取消 Like")
        void toggleLike_removeLike() {
            when(likeRepo.existsByProductReview_IdAndUserMember_Id(3, 1)).thenReturn(true);
            when(likeRepo.countByProductReview_Id(3)).thenReturn(5);

            int count = reviewService.toggleLike(3, 1);

            verify(likeRepo).deleteByReviewIdAndUserId(3, 1);
            verify(reviewRepo).decrementHelpfulCount(3);
            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("toggleLike() - 尚未點讚，應新增 Like")
        void toggleLike_addLike() {
            when(likeRepo.existsByProductReview_IdAndUserMember_Id(3, 1)).thenReturn(false);
            when(likeRepo.countByProductReview_Id(3)).thenReturn(6);
            when(reviewRepo.getReferenceById(3)).thenReturn(new ProductReview());

            int count = reviewService.toggleLike(3, 1);

            verify(likeRepo).save(any());
            verify(reviewRepo).incrementHelpfulCount(3);
            assertThat(count).isEqualTo(6);
        }
    }

    @Nested
    @DisplayName("createReview()")
    class CreateReview {

        @Test
        @DisplayName("createReview() - 標籤超過3個，應丟 IllegalArgumentException")
        void createReview_tagsExceed_throw() {
            ReviewCreateReq req = new ReviewCreateReq(
                1, 1001, "太棒了！", List.of(),
                5, 5, 5,
                Set.of(ReviewTag.FAST, ReviewTag.QUALITY, ReviewTag.VALUE, ReviewTag.SERVICE)
            );

            assertThatThrownBy(() -> reviewService.createReview(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("標籤不可超過");
        }

        @Test
        @DisplayName("createReview() - 成功新增一筆評論")
        void createReview_success() {
            ReviewCreateReq req = new ReviewCreateReq(
                1, 1001, "不錯喔～", List.of("img1.jpg"),
                5, 4, 5,
                Set.of(ReviewTag.FAST, ReviewTag.QUALITY)
            );

            reviewService.createReview(req);

            verify(reviewRepo).save(any(ProductReview.class));
        }
    }

    @Nested
    @DisplayName("deleteReview()")
    class DeleteReview {

        @Test
        @DisplayName("成功刪除一篇評論")
        void deleteReview_success() {
            reviewService.deleteReview(5);
            verify(reviewRepo).deleteById(5);
        }
    }

    @Nested
    @DisplayName("updateReviewText()")
    class UpdateReviewText {

        @Test
        @DisplayName("成功更新評論文字")
        void updateReviewText_success() {
            ProductReview review = new ProductReview();
            when(reviewRepo.findById(10)).thenReturn(Optional.of(review));

            reviewService.updateReviewText(10, "新的評論文字");

            assertThat(review.getReviewText()).isEqualTo("新的評論文字");
        }

        @Test
        @DisplayName("找不到評論時丟出例外")
        void updateReviewText_notFound_throw() {
            when(reviewRepo.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.updateReviewText(999, "test"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Review not found");
        }
    }

    @Nested
    @DisplayName("hideReview()")
    class HideReview {

        @Test
        @DisplayName("成功隱藏一篇評論")
        void hideReview_success() {
            ProductReview review = new ProductReview();
            review.setIsVisible(true);
            when(reviewRepo.findById(20)).thenReturn(Optional.of(review));

            reviewService.hideReview(20);

            assertThat(review.getIsVisible()).isFalse();
        }

        @Test
        @DisplayName("找不到評論時丟出例外")
        void hideReview_notFound_throw() {
            when(reviewRepo.findById(888)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.hideReview(888))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Review not found");
        }
    }
}
