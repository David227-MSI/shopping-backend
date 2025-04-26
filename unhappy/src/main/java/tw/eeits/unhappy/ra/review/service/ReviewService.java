package tw.eeits.unhappy.ra.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import tw.eeits.unhappy.ra.review.dto.*;
import tw.eeits.unhappy.ra.review.model.*;
import tw.eeits.unhappy.ra.review.repository.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductReviewRepository reviewRepo;
    private final ReviewLikeRepository    likeRepo;

    /* ---------- Create ---------- */

    @Transactional
    public ReviewResp addReview(Integer userId, Integer orderItemId, ReviewCreateReq req) {

        if (req.tags().size() > 3)
            throw new IllegalArgumentException("最多勾選 3 個標籤");

        if (reviewRepo.existsByUserIdAndOrderItemId(userId, orderItemId))
            throw new IllegalStateException("同一筆訂單只能評論一次");

        // TODO: 驗證 orderItem 是否屬於 user 且已完成 → isVerifiedPurchase
        boolean verified = true;

        ProductReview pr = new ProductReview();
        pr.setUserId(userId);
        pr.setOrderItemId(orderItemId);
        pr.setReviewText(req.reviewText());
        pr.setReviewImages(req.reviewImages());
        pr.setScoreQuality(req.scoreQuality());
        pr.setScoreDescription(req.scoreDescription());
        pr.setScoreDelivery(req.scoreDelivery());
        pr.setIsVerifiedPurchase(verified);
        pr.setTagName(req.tags());

        ProductReview saved = reviewRepo.save(pr);
        return toResp(saved);
    }

    /* ---------- Read ---------- */

    public Page<ReviewResp> listByProduct(Integer productId,
    ReviewSortOption option,
    int page, int size) {

    Sort sort = switch (option) {
    case LATEST      -> Sort.by(Sort.Direction.DESC, "createdAt");
    case MOST_LIKED  -> Sort.by(Sort.Direction.DESC, "helpfulCount");
    case WITH_IMAGES -> Sort.by(Sort.Direction.DESC, "createdAt");
    };
    Pageable pageable = PageRequest.of(page, size, sort);
    
    Page<ProductReview> pageEntity =
    (option == ReviewSortOption.WITH_IMAGES)
    ? reviewRepo.findVisibleWithImagesByProduct(productId, pageable)
    : reviewRepo.findVisibleByProduct(productId, pageable);
    
    return pageEntity.map(this::toResp);
    }

    public ReviewResp getOne(Integer reviewId) {
        return toResp(reviewRepo.findById(reviewId)
                                .orElseThrow(() ->
                                    new IllegalArgumentException("評論不存在")));
    }

    /* ---------- Update ---------- */

    @Transactional
    public ReviewResp updateReview(Integer reviewId, Integer userId, ReviewCreateReq req) {

        ProductReview pr = reviewRepo.findById(reviewId)
                                    .orElseThrow(() -> new IllegalArgumentException("評論不存在"));
        if (!pr.getUserId().equals(userId))
            throw new SecurityException("只能修改自己的評論");

        if (req.tags().size() > 3)
            throw new IllegalArgumentException("最多勾選 3 個標籤");

        pr.setReviewText(req.reviewText());
        pr.setReviewImages(req.reviewImages());
        pr.setScoreQuality(req.scoreQuality());
        pr.setScoreDescription(req.scoreDescription());
        pr.setScoreDelivery(req.scoreDelivery());
        pr.setTagName(req.tags());

        return toResp(pr);        // 依賴 JPA flush
    }

    /* ---------- Delete (軟刪) ---------- */

    @Transactional
    public void deleteReview(Integer reviewId, Integer userId) {
        ProductReview pr = reviewRepo.findById(reviewId)
                                    .orElseThrow();
        if (!pr.getUserId().equals(userId))
            throw new SecurityException("只能刪除自己的評論");
        pr.setIsVisible(false);
    }

    /* ---------- 後台審核隱藏評論 ---------- */

    @Transactional
    public void adminSetVisible(Integer reviewId, boolean visible) {
        ProductReview r = reviewRepo.findById(reviewId)
                                    .orElseThrow(() -> new IllegalArgumentException("評論不存在"));
        r.setIsVisible(visible);
    }

    /* ---------- Like / Unlike ---------- */

    @Transactional
    public int toggleLike(Integer reviewId, Integer userId) {
        try {
            if (likeRepo.existsByProductReviewIdAndUserId(reviewId, userId)) {
                likeRepo.deleteByProductReviewIdAndUserId(reviewId, userId);
                reviewRepo.decrementHelpfulCount(reviewId);
            } else {
                ReviewLike like = new ReviewLike();
                like.setProductReview(reviewRepo.getReferenceById(reviewId));
                like.setUserId(userId);
                likeRepo.save(like);              // 這裡若撞 UNIQUE 會丟異常
                reviewRepo.incrementHelpfulCount(reviewId);
            }
        } catch (DataIntegrityViolationException e) {
            // 代表別的執行緒先插入了，這裡直接「重試一次」即可
            return toggleLike(reviewId, userId);
        }
        return likeRepo.countByProductReviewId(reviewId);
    }

    /* ---------- helper ---------- */
    private ReviewResp toResp(ProductReview r) {
        return new ReviewResp(
                r.getId(),
                r.getUserId(),
                r.getOrderItemId(),
                r.getReviewText(),
                r.getReviewImages(),
                r.getScoreQuality(),
                r.getScoreDescription(),
                r.getScoreDelivery(),
                r.getIsVerifiedPurchase(),
                r.getTagName(),
                r.getHelpfulCount(),
                r.getCreatedAt()
        );
    }
}
