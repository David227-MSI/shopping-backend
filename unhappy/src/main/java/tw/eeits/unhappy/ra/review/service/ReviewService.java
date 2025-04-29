package tw.eeits.unhappy.ra.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.eeits.unhappy.ra.review.dto.PageDto;
import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewLike;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.repository.ProductReviewRepository;
import tw.eeits.unhappy.ra.review.repository.ReviewLikeRepository;
import tw.eeits.unhappy.ra._fake.OrderItem;
import tw.eeits.unhappy.ra._fake.UserMember;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductReviewRepository reviewRepo;
    private final ReviewLikeRepository likeRepo;

    public PageDto<ReviewResp> listByProduct(Integer productId, ReviewSortOption option, int page, int size) {
        Sort sort = switch (option) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case MOST_LIKED -> Sort.by(Sort.Direction.DESC, "helpfulCount");
            case WITH_IMAGES -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductReview> pageEntity =
                (option == ReviewSortOption.WITH_IMAGES)
                        ? reviewRepo.findByProductIdAndIsVisibleAndReviewImagesIsNotNull(productId, true, pageable)
                        : reviewRepo.findByProductIdAndIsVisible(productId, true, pageable);

        return PageDto.from(pageEntity.map(this::toResp));
    }

    private ReviewResp toResp(ProductReview r) {
        int realLikeCount = likeRepo.countByProductReview_Id(r.getId());
        return new ReviewResp(
                r.getId(),
                r.getUserMember().getId(),
                r.getOrderItem().getId(),
                r.getReviewText(),
                r.getReviewImages() == null ? List.of() : r.getReviewImages(),
                r.getScoreQuality(),
                r.getScoreDescription(),
                r.getScoreDelivery(),
                r.getIsVerifiedPurchase(),
                r.getTagName() == null ? List.of() : r.getTagName().stream().map(Enum::name).toList(),
                realLikeCount,
                r.getCreatedAt()
        );
    }

    @Transactional
    public int toggleLike(Integer reviewId, Integer userId) {
        boolean alreadyLiked = likeRepo.existsByProductReview_IdAndUserMember_Id(reviewId, userId);

        if (alreadyLiked) {
            likeRepo.deleteByReviewIdAndUserId(reviewId, userId);
            reviewRepo.decrementHelpfulCount(reviewId);
        } else {
            ReviewLike like = new ReviewLike();
            like.setProductReview(reviewRepo.getReferenceById(reviewId));
            UserMember user = new UserMember();
            user.setId(userId);
            like.setUserMember(user);
            likeRepo.save(like);
            reviewRepo.incrementHelpfulCount(reviewId);
        }

        return likeRepo.countByProductReview_Id(reviewId);
    }

    @Transactional
    public void createReview(ReviewCreateReq req) {
        if (req.tags() != null && req.tags().size() > 3) {
            throw new IllegalArgumentException("評論標籤不可超過3個");
        }

        ProductReview review = new ProductReview();
        review.setReviewText(req.reviewText());
        review.setReviewImages(req.reviewImages());
        review.setScoreQuality(req.scoreQuality());
        review.setScoreDescription(req.scoreDescription());
        review.setScoreDelivery(req.scoreDelivery());
        review.setTagName(req.tags());

        UserMember user = new UserMember();
        user.setId(req.userId());
        review.setUserMember(user);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(req.orderItemId());
        review.setOrderItem(orderItem);

        reviewRepo.save(review);
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        reviewRepo.deleteById(reviewId);
    }

    @Transactional
    public void updateReviewText(Integer reviewId, String newText) {
        ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setReviewText(newText);
    }

    @Transactional
    public void hideReview(Integer reviewId) {
        ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setIsVisible(false);
    }
}
