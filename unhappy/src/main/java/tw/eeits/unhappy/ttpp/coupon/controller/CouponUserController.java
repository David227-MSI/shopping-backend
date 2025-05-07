package tw.eeits.unhappy.ttpp.coupon.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponTransferRequest;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;


@RestController
@RequestMapping("/api/user/coupons")
@RequiredArgsConstructor
public class CouponUserController {

    private final CouponService couponService;
    private final Validator validator;

   
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/query")
    public ResponseEntity<ApiRes<Map<String, Object>>> findUserCoupons(
        @RequestBody CouponQuery query) {
        
        // call service
        ServiceResponse<List<CouponPublished>> res = couponService.findCouponsByCriteria(query);

        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        List<CouponPublished> foundData = res.getData();
        List<Map<String, Object>> couponList = foundData.stream().map(coupon -> {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", coupon.getId());
            mp.put("isUsed", coupon.getIsUsed());
            mp.put("applicableId", coupon.getCouponTemplate().getApplicableId());
            mp.put("applicableType", coupon.getCouponTemplate().getApplicableType());
            mp.put("discountType", coupon.getCouponTemplate().getDiscountType());
            mp.put("discountValue", coupon.getCouponTemplate().getDiscountValue());
            mp.put("tradeable", coupon.getCouponTemplate().getTradeable());
            mp.put("startTime", coupon.getCouponTemplate().getStartTime());
            mp.put("endTime", coupon.getCouponTemplate().getEndTime());
            mp.put("couponMedia", coupon.getCouponTemplate().getCouponMedia());
            return mp;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("couponList", couponList);



        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiRes<Map<String, Object>>> couponTransfer(
        @RequestBody CouponTransferRequest request) {
        
        ErrorCollector ec = new ErrorCollector();
        
        // verify data type
        ec.validate(request, validator);

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<CouponPublished> res = couponService.couponTransfer(request.getCouponId(), request.getRecipientMail());
        
        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(res.getMessage()));
        }
        
        CouponPublished resCoupon = res.getData();

        Map<String, Object> data = new HashMap<>();
        data.put("id", resCoupon.getId());
        data.put("ownerMail", resCoupon.getUserMember().getEmail());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================

}
