package tw.eeits.unhappy.eeit198product.dto;

import lombok.Data;
import tw.eeits.unhappy.ra.review.model.ReviewTag;

import java.util.List;
import java.util.Set;

@Data
public class ProductReviewDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer orderItemId;
    private String reviewText;
    private List<String> reviewImages; // List 不是 String
    private Integer scoreQuality;
    private Integer scoreDescription;
    private Integer scoreDelivery;
    private Boolean verified;
    private Boolean visible;
    private Set<ReviewTag> tagName;
    private String createdAt;
    

}
