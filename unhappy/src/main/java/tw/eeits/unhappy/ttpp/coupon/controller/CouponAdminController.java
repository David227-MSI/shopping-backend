package tw.eeits.unhappy.ttpp.coupon.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.repository.BrandRepository;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponPublishedRequest;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponTemplateRequest;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.media.dto.MediaRequest;
import tw.eeits.unhappy.ttpp.media.model.CouponMedia;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class CouponAdminController {

    private final CouponService couponService;
    private final UserMemberService userMemberService;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final Validator validator;

    // =================================================================
    // 建立優惠相關======================================================
    // =================================================================
    @PostMapping("/template")
    public ResponseEntity<ApiRes<Map<String, Object>>> createTemplate(
        @RequestBody CouponTemplateRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);
        
        // verify foreign key (Brand, Product)
        if(request.getApplicableType() == ApplicableType.PRODUCT) {
            Product foundProduct = productRepository.findById(request.getApplicableId()).orElse(null);
            if(foundProduct == null) {ec.add("找不到目標商品");}
        } else if(request.getApplicableType() == ApplicableType.BRAND) {
            Brand foundBrand = brandRepository.findById(request.getApplicableId()).orElse(null);
            if(foundBrand == null) {ec.add("找不到目標廠商");}
        }

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

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

        // call service
        ServiceResponse<CouponTemplate> res = couponService.createTemplate(newEntry);

        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        CouponTemplate savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("applicableType", savedEntry.getApplicableType());
        data.put("discountType", savedEntry.getDiscountType());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiRes<Map<String, Object>>> publish(
        @RequestBody CouponPublishedRequest request
    ) {

        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);
        
        // check foreign key
        CouponTemplate foundTemplate = couponService.findTemplateById(request.getCouponTemplateId());
        UserMember foundUser = userMemberService.findUserById(request.getUserId());

        if(foundTemplate == null) {ec.add("找不到套用的優惠券模板");}
        if(foundUser == null) {ec.add("找不到目標用戶");}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
        CouponPublished newEntry = CouponPublished.builder()
                .userMember(foundUser)
                .couponTemplate(foundTemplate)
                .isUsed(false)
                .build();

        // call service
        ServiceResponse<CouponPublished> res = couponService.publishCoupon(newEntry);

        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        CouponPublished savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("userId", savedEntry.getUserMember().getId());
        data.put("applicableType", savedEntry.getCouponTemplate().getApplicableType());
        data.put("discountType", savedEntry.getCouponTemplate().getDiscountType());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }



    @PostMapping("/uploadImage")
    public ResponseEntity<?> addMediaToTemplate(MediaRequest request) {

        ErrorCollector ec = new ErrorCollector();

        if(request == null) {
            ec.add("請輸入請求資料");
        } else {
            ec.validate(request, validator);
        }

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        try {
            ServiceResponse<CouponMedia> res = couponService.addMediaToTemplate(request);

            if (!res.isSuccess()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseFactory.fail(res.getMessage()));
            } 
            
            return ResponseEntity.ok(res);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceResponse.fail("圖片處理失敗: " + e.getMessage()));
        }
    }



    // =================================================================
    // 建立優惠相關======================================================
    // =================================================================


    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    
    @GetMapping("/templates/{id}")
    public ResponseEntity<ApiRes<CouponTemplate>> findTemplateById(@PathVariable Integer id) {
        CouponTemplate foundEntry = couponService.findTemplateById(id);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }

    @PostMapping("/templates/findAll")
    public ResponseEntity<ApiRes<Map<String, Object>>> findAllTemplates(
        @RequestBody CouponQuery query) {

        // call service
        ServiceResponse<List<CouponTemplate>> res = couponService.findTemplatesByCriteria(query);
        
        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        List<CouponTemplate> foundData = res.getData();
        List<Map<String, Object>> templateList = foundData.stream().map(template -> {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", template.getId());
            mp.put("applicableId", template.getApplicableId());
            mp.put("applicableType", template.getApplicableType());
            mp.put("discountValue", template.getDiscountValue());
            mp.put("startTime", template.getStartTime());
            mp.put("endTime", template.getEndTime());
            mp.put("couponMedia", template.getCouponMedia());
            return mp;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("templateList", templateList);
        
        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
 




}
