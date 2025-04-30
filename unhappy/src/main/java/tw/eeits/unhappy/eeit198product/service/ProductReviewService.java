package tw.eeits.unhappy.eeit198product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import tw.eeits.unhappy.eeit198product.dto.ProductReviewDTO;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ra.review.repository.ProductReviewRepository;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); // 用來解析 JSON

    public List<ProductReviewDTO> findByProductId(Integer productId) {
        List<ProductReview> reviews = productReviewRepository.findReviewsByProductId(productId);
        List<ProductReviewDTO> dtos = new ArrayList<>();

        for (ProductReview review : reviews) {
            ProductReviewDTO dto = new ProductReviewDTO();
            dto.setId(review.getId());
            dto.setUserId(review.getUserMember().getId());
            dto.setOrderItemId(review.getOrderItem().getId());
            dto.setReviewText(review.getReviewText());
            dto.setScoreQuality(review.getScoreQuality());
            dto.setScoreDescription(review.getScoreDescription());
            dto.setScoreDelivery(review.getScoreDelivery());
            dto.setVerified(review.getIsVerifiedPurchase());
            dto.setVisible(review.getIsVisible());
            dto.setTagName(review.getTagName());
            dto.setCreatedAt(review.getCreatedAt() != null ? review.getCreatedAt().toString() : null);

            try {
                if (review.getReviewImages() != null) {
                    dto.setReviewImages(review.getReviewImages());
                } else {
                    dto.setReviewImages(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setReviewImages(new ArrayList<>());
            }

            dtos.add(dto);
        }

        return dtos;
    }
}
