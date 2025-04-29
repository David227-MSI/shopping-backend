package tw.eeits.unhappy.ra.review.dto;

import java.time.LocalDateTime;
import java.util.List;

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
        List<String> tags,
        Integer likeCount,
        LocalDateTime createdAt
) {

}