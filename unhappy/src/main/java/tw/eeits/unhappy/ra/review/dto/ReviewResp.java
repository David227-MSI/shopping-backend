package tw.eeits.unhappy.ra.review.dto;

import tw.eeits.unhappy.ra.review.model.ReviewTag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ReviewResp(
        Integer id,
        Integer userId,
        Integer orderItemId,
        String  reviewText,
        List<String> reviewImages,
        Integer scoreQuality,
        Integer scoreDescription,
        Integer scoreDelivery,
        Boolean isVerifiedPurchase,
        Set<ReviewTag> tags,
        Integer likeCount,
        LocalDateTime createdAt
) {

}