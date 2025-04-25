package tw.eeits.unhappy.ttpp.coupon.controller;



import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponPublishedRequest;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponTemplateRequest;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;



@RestController
@RequestMapping("/app/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final Validator validator;


    // =================================================================
    // 建立優惠相關======================================================
    // =================================================================
    @PostMapping("/template")
    public ResponseEntity<ApiRes<CouponTemplate>> createTemplate(
        @RequestBody CouponTemplateRequest request
    ) {

        // verify data type
        Set<ConstraintViolation<CouponTemplateRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
        }

        // check product

        // check brand

        // transfer data from DTO to Entity
        CouponTemplate newEntry = CouponTemplate.builder()
                .applicableId(request.getApplicableId())
                .applicableType(request.getApplicableType())
                .minSpend(request.getMinSpend())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscount(request.getMaxDiscount())
                .tradeable(request.getTradeable())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
            CouponTemplate savedEntry = couponService.createTemplate(newEntry);

        // create template
        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiRes<CouponPublished>> publish(
        @RequestBody CouponPublishedRequest request
    ) {
        
        // verify data type
        Set<ConstraintViolation<CouponPublishedRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
        }

        // check couponTemplate
        CouponTemplate foundTemplate = couponService.findTemplateById(request.getCouponTemplateId());
        
        // check User

        // transfer data from DTO to Entity
        CouponPublished newEntry = CouponPublished.builder()
                .userId(request.getUserId())
                .couponTemplate(foundTemplate)
                .isUsed(false)
                .build();
        CouponPublished savedEntry = couponService.publishCoupon(newEntry);

        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }
    // =================================================================
    // 建立優惠相關======================================================
    // =================================================================





    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    
    @GetMapping("/templates/{id}")
    public ResponseEntity<ApiRes<CouponTemplate>> getMethodName(@PathVariable Integer id) {
        CouponTemplate foundEntry = couponService.findTemplateById(id);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }

    @PostMapping("/templates/findAll")
    public ResponseEntity<ApiRes<List<CouponTemplate>>> findAllTemplates(@RequestBody CouponQuery query) {
        List<CouponTemplate> foundEntry = couponService.findTemplatesByCriteria(query);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    



    
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/user/query")
    public ResponseEntity<ApiRes<List<CouponPublished>>> findUserCoupons(@RequestBody CouponQuery query) {
        List<CouponPublished> foundEntry = couponService.findCouponsByCriteria(query);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    


}
