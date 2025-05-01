package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.service.ProductService;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.service.BrandService;
import tw.eeits.unhappy.ttpp._itf.SubscribeListService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.SubscribeListRequest;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;


@RestController
@RequestMapping("/api/subscribes")
@RequiredArgsConstructor
public class SubscribeListController {

    private final SubscribeListService subscribeService;
    private final UserMemberService userMemberService;
    private final ProductService productService;
    private final BrandService brandService;
    private final Validator validator;

    @PostMapping("/switch")
    public ResponseEntity<ApiRes<Map<String, Object>>> subscribeSwitch(
        @RequestBody SubscribeListRequest request) {

        ErrorCollector ec = new ErrorCollector();

        // verify request data
        ec.validate(request, validator);

        // check foreign key
        UserMember foundUser = userMemberService.findUserById(request.getUserId());
        Product foundProduct = null;
        Brand foundBrand = null;

        if(foundUser == null) {ec.add("找不到用戶資訊");}
        if(request.getItemType() == ItemType.PRODUCT) {
            foundProduct = productService.getProductById(request.getItemId()).orElse(null);
            if(foundProduct == null) {ec.add("找不到追蹤商品");}
        }
        if(request.getItemType() == ItemType.BRAND) {
            foundBrand = brandService.findBrandById(request.getItemId());
            if(foundBrand == null) {ec.add("找不到追蹤廠商");}
        }


        if (ec.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        SubscribeList newEntry = SubscribeList.builder()
            .userMember(foundUser)
            .itemId(request.getItemId())
            .itemType(request.getItemType())
            .build();

        // call service
        ServiceResponse<SubscribeList> res = subscribeService.subscribeSwitch(newEntry);
        if (!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        SubscribeList result = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", result.getId());
        data.put("userId", result.getUserMember().getId());
        data.put("itemId", result.getItemId());
        data.put("itemType", result.getItemType());
        data.put("isSubscribing", result.getIsSubscribing());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    


    
}
