package tw.eeits.unhappy.ra.review.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.gy.domain.OrderItem;
import tw.eeits.unhappy.ra.review.dto.AverageScoresDto;
import tw.eeits.unhappy.ra.review.dto.PageDto;
import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.model.ReviewLike;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.model.ReviewTag;
import tw.eeits.unhappy.ra.review.repository.ProductReviewRepository;
import tw.eeits.unhappy.ra.review.repository.ReviewLikeRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public AverageScoresDto getAverageScoresByProduct(Integer productId) {
        List<ProductReview> reviews = reviewRepo.findReviewsByProductId(productId);
        if (reviews.isEmpty()) {
            return new AverageScoresDto(0.0, 0.0, 0.0);
        }

        double totalQuality = 0.0;
        double totalDescription = 0.0;
        double totalDelivery = 0.0;
        int count = reviews.size();

        for (ProductReview review : reviews) {
            totalQuality += review.getScoreQuality();
            totalDescription += review.getScoreDescription();
            totalDelivery += review.getScoreDelivery();
        }

        return new AverageScoresDto(
            totalQuality / count,
            totalDescription / count,
            totalDelivery / count
        );
    }

    public ReviewResp findByOrderItemIdAndUserId(Integer orderItemId, Integer userId) {
        ProductReview review = reviewRepo.findByOrderItemIdAndUserId(orderItemId, userId)
                .orElse(null);
        return review != null ? toResp(review) : null;
    }

    public ReviewResp findById(Integer id) {
        ProductReview review = reviewRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return toResp(review);
    }

    private ReviewResp toResp(ProductReview r) {
        int realLikeCount = likeRepo.countByProductReview_Id(r.getId());
        return new ReviewResp(
                r.getId(),
                r.getUserMember().getId(),
                r.getUserMember().getUsername(),
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
        boolean exists = reviewRepo.existsByOrderItemIdAndUserId(req.orderItemId(), req.userId());
        if (exists) {
            log.warn("評論已存在: orderItemId={}, userId={}", req.orderItemId(), req.userId());
            throw new IllegalArgumentException("您已對此訂單項目提交過評論");
        }

        if (req.tags() != null && req.tags().size() > 3) {
            log.warn("標籤數量超過限制: {}", req.tags().size());
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
        log.info("評論儲存成功: orderItemId={}, userId={}", req.orderItemId(), req.userId());
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
        log.info("評論文字更新成功: reviewId={}", reviewId);
    }

    @Transactional
    public void updateReview(Integer reviewId, String reviewText, List<String> tags, Integer qualityScore, Integer descriptionScore, Integer shippingScore) {
        ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setReviewText(reviewText);
        review.setScoreQuality(qualityScore);
        review.setScoreDescription(descriptionScore);
        review.setScoreDelivery(shippingScore);
        log.info("更新評論分數: reviewId={}, quality={}, description={}, delivery={}",
                                    reviewId, qualityScore, descriptionScore, shippingScore);
        if (tags != null) {
            if (tags.size() > 3) {
                log.warn("標籤數量超過限制: {}", tags.size());
                throw new IllegalArgumentException("評論標籤不可超過3個");
            }
            Set<ReviewTag> reviewTags = tags.stream()
                    .map(ReviewTag::fromLabel)
                    .collect(Collectors.toSet());
            review.setTagName(reviewTags);
            log.info("標籤更新: reviewId={}, tags={}", reviewId, reviewTags);
        }
        reviewRepo.save(review);
        log.info("評論更新成功: reviewId={}", reviewId);
    }

    @Transactional
    public void hideReview(Integer reviewId) {
        ProductReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setIsVisible(false);
    }
}