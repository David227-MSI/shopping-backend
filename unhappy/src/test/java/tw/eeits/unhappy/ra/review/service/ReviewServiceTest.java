package tw.eeits.unhappy.ra.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewLike;
import tw.eeits.unhappy.ra.review.model.ReviewTag;
import tw.eeits.unhappy.ra.review.repository.ProductReviewRepository;
import tw.eeits.unhappy.ra.review.repository.ReviewLikeRepository;

/**
 * ReviewService 的單元測試
 *
 * 採 Mockito 模擬 Repository，
 *   - 不連資料庫
 *   - 只關注 Service 的商業邏輯
 *
 * 測試重點：
 *   1) addReview() 成功 / 失敗
 *   2) toggleLike() 點讚 / 取消
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock  ProductReviewRepository reviewRepo;
    @Mock  ReviewLikeRepository    likeRepo;
    @InjectMocks ReviewService     reviewService;

    /* =====================================================
     * addReview()
     * ===================================================*/

    @Test
    @DisplayName("addReview() ‑ 正常新增一筆評論")
    void addReview_success() {
        // Arrange ------------------------------------------------------
        ReviewCreateReq req = new ReviewCreateReq(
                "很好用！",                // reviewText
                null,                      // reviewImages
                5, 5, 5,                   // 3 scores
                Set.of(ReviewTag.FAST, ReviewTag.QUALITY) // 2 tags
        );

        when(reviewRepo.existsByUserIdAndOrderItemId(1001, 7))
                .thenReturn(false);        // 尚未評論

        ProductReview saved = new ProductReview();
        saved.setId(123);
        when(reviewRepo.save(any(ProductReview.class))).thenReturn(saved);

        // Act ----------------------------------------------------------
        ReviewResp resp = reviewService.addReview(1001, 7, req);

        // Assert -------------------------------------------------------
        assertThat(resp.id()).isEqualTo(123);
        verify(reviewRepo).save(any(ProductReview.class));
    }

    @Test
    @DisplayName("addReview() ‑ 標籤超過 3 個應丟 IllegalArgumentException")
    void addReview_tooManyTags_throw() {
        ReviewCreateReq req = new ReviewCreateReq(
                "", null, 5, 5, 5,
                Set.of(ReviewTag.FAST, ReviewTag.QUALITY, ReviewTag.VALUE, ReviewTag.SERVICE)
        );

        assertThatThrownBy(() -> reviewService.addReview(1, 1, req))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(reviewRepo, likeRepo);
    }

    /* =====================================================
     * toggleLike()
     * ===================================================*/

    @Test
    @DisplayName("toggleLike() ‑ 尚未點讚 → 新增 Like")
    void toggleLike_add() {
        when(likeRepo.existsByProductReviewIdAndUserId(3, 99)).thenReturn(false);
        when(reviewRepo.getReferenceById(3)).thenReturn(new ProductReview());
        when(likeRepo.countByProductReviewId(3)).thenReturn(1);

        int newCount = reviewService.toggleLike(3, 99);

        verify(likeRepo).save(any(ReviewLike.class));
        verify(reviewRepo).incrementHelpfulCount(3);
        assertThat(newCount).isEqualTo(1);
    }

    @Test
    @DisplayName("toggleLike() ‑ 已點讚 → 取消 Like")
    void toggleLike_remove() {
        when(likeRepo.existsByProductReviewIdAndUserId(3, 99)).thenReturn(true);
        when(likeRepo.countByProductReviewId(3)).thenReturn(0);

        int newCount = reviewService.toggleLike(3, 99);

        verify(likeRepo).deleteByProductReviewIdAndUserId(3, 99);
        verify(reviewRepo).decrementHelpfulCount(3);
        assertThat(newCount).isEqualTo(0);
    }
}

