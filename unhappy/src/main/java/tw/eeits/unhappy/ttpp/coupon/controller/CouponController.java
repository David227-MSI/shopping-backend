package tw.eeits.unhappy.ttpp.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponTemplateRequest;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;

@RestController
@RequestMapping("/app/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/template")
    public ResponseEntity<CouponTemplate> createTemplate(
        @RequestBody CouponTemplateRequest request
    ) {
        System.out.println(1);
        // transfer data from DTO to Entity
        CouponTemplate template = CouponTemplate.builder()
                // .id(request.getId())
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
        // create template
        return ResponseEntity.ok(couponService.createTemplate(template));
    }
    
    









}
