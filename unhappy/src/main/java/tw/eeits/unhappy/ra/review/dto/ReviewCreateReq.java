package tw.eeits.unhappy.ra.review.dto;

import jakarta.validation.constraints.*;
import tw.eeits.unhappy.ra.review.model.ReviewTag;

import java.util.List;
import java.util.Set;

public record ReviewCreateReq(
        @NotNull               Integer userId,
        @NotNull               Integer orderItemId,
        @Size(max = 1000)      String  reviewText,
        List<String>           reviewImages,
        @NotNull @Min(1) @Max(5) Integer scoreQuality,
        @NotNull @Min(1) @Max(5) Integer scoreDescription,
        @NotNull @Min(1) @Max(5) Integer scoreDelivery,
        @Size(max = 3) @NotNull  Set<ReviewTag> tags
) {

}