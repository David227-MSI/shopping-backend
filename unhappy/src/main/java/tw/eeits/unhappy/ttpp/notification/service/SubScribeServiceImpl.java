package tw.eeits.unhappy.ttpp.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.repository.BrandRepository;
import tw.eeits.unhappy.ttpp._itf.SubscribeListService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.SubscribeListRequest;
import tw.eeits.unhappy.ttpp.notification.dto.SubscribeQuery;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;
import tw.eeits.unhappy.ttpp.notification.repository.SubscribeListRepository;

@Service
@RequiredArgsConstructor
public class SubScribeServiceImpl implements SubscribeListService {

    private final SubscribeListRepository subscribeListRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final UserMemberRepository userMemberRepository;
    private final Validator validator;

    // =================================================================
    // 建立相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<SubscribeList> subscribeSwitch(SubscribeList subscribe) {

        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if (subscribe == null) {
            ec.add("請提供訂閱資訊");
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        if (subscribe.getUserMember() == null) {
            ec.add("用戶 為必要欄位");
        }
        if (subscribe.getItemId() == null) {
            ec.add("追蹤商品/廠商ID 為必要欄位");
        }
        if (subscribe.getItemType() == null) {
            ec.add("追蹤物件類型 為必要欄位");
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            // search record
            SubscribeList foundEntry = subscribeListRepository.findByUserMemberAndItemTypeAndItemId(
                subscribe.getUserMember(), 
                subscribe.getItemType(), 
                subscribe.getItemId()
            );

            if (foundEntry != null) {
                // if record exists, switch subscribe status
                foundEntry.setIsSubscribing(!foundEntry.getIsSubscribing());
                SubscribeList updated = subscribeListRepository.save(foundEntry);
                boolean status = updated.getIsSubscribing();
                return ServiceResponse.success("追蹤狀態修改為: " + status, updated);
            } else {
                // create new record if not found
                subscribe.setIsSubscribing(true);
                subscribe.setCreatedAt(LocalDateTime.now());
                SubscribeList created = subscribeListRepository.save(subscribe);
                boolean status = created.getIsSubscribing();
                return ServiceResponse.success("追蹤狀態修改為: " + status, created);
            }
        } catch (Exception e) {
            return ServiceResponse.fail("訂閱操作失敗: " + e.getMessage());
        }
    }
    // =================================================================
    // 建立相關==========================================================
    // =================================================================
    
    @Override
    public ServiceResponse<Boolean> getSubscribeStatus(SubscribeListRequest request) {
        ErrorCollector ec = new ErrorCollector();

        ec.validate(request, validator);

        // service operation
        try {
            Boolean res = subscribeListRepository.existsByUserMemberIdAndItemTypeAndItemIdAndIsSubscribing(
                    request.getUserId(), 
                    request.getItemType(), 
                    request.getItemId(), 
                    true
            );
            return ServiceResponse.success(res);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢追蹤狀態發生異常: " + e);
        }
    }



    @Override
    public ServiceResponse<Map<String, Object>> findSubscribedProducts(SubscribeQuery query) {
        
        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = userMemberRepository.findById(query.getUserId()).orElse(null);

        if(foundUser == null) {ec.add("找不到該用戶");}

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            List<SubscribeList> foundEntry = subscribeListRepository.findByUserMemberAndItemTypeAndIsSubscribing(
                foundUser, ItemType.PRODUCT, true);
            Map<String, Object> data = new HashMap<>();

            List<Integer> productIds = foundEntry.stream()
                    .map(SubscribeList::getItemId) // 假設 SubscribeList 的 itemId 對應 Product 的 id
                    .collect(Collectors.toList());

            List<Product> products = new ArrayList<>();
            if(!productIds.isEmpty()) {
                products = productRepository.findSubscribedProductsByCondition(
                        query.getCategoryId(),
                        query.getKeyword(),
                        productIds
                );
            }
            data.put("products", products);
            return ServiceResponse.success(data);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢追蹤商品發生異常: " + e.getMessage());
        }
                
    }

    @Override
    public ServiceResponse<Map<String, Object>> findSubscribedBrands(SubscribeQuery query) {
        
        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = userMemberRepository.findById(query.getUserId()).orElse(null);

        if(foundUser == null) {ec.add("找不到該用戶");}

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            List<SubscribeList> foundEntry = subscribeListRepository.findByUserMemberAndItemTypeAndIsSubscribing(
                foundUser, ItemType.BRAND, true);
            Map<String, Object> data = new HashMap<>();

            List<Integer> brandIds = foundEntry.stream()
                    .map(SubscribeList::getItemId) // 假設 SubscribeList 的 itemId 對應 Product 的 id
                    .collect(Collectors.toList());

            List<Brand> brands = new ArrayList<>();
            if(!brandIds.isEmpty()) {
                brands = brandRepository.findSubscribedBrandsByCondition(
                    query.getKeyword(), 
                    brandIds
                );
            }
            data.put("brands", brands);
            return ServiceResponse.success(data);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢追蹤品牌發生異常: " + e.getMessage());
        }
                
    }

    @Override
    public ServiceResponse<Map<String, Object>> findSubscribedItems(SubscribeQuery query) {

        ErrorCollector ec = new ErrorCollector();
        UserMember foundUser = userMemberRepository.findById(query.getUserId()).orElse(null);

        if (foundUser == null) {
            ec.add("找不到該用戶");
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        try {
            Map<String, Object> data = new HashMap<>();

            // 查詢商品
            List<SubscribeList> productEntries = subscribeListRepository.findByUserMemberAndItemTypeAndIsSubscribing(
                foundUser, ItemType.PRODUCT, true);
            List<Integer> productIds = productEntries.stream()
                    .map(SubscribeList::getItemId)
                    .collect(Collectors.toList());

            if (!productIds.isEmpty()) {
                List<Product> products = productRepository.findSubscribedProductsByCondition(
                    query.getCategoryId(), query.getKeyword(), productIds);
                data.put("products", products);
            } else {
                data.put("products", Collections.emptyList());
            }

            // 查詢品牌
            List<SubscribeList> brandEntries = subscribeListRepository.findByUserMemberAndItemTypeAndIsSubscribing(
                foundUser, ItemType.BRAND, true);
            List<Integer> brandIds = brandEntries.stream()
                    .map(SubscribeList::getItemId)
                    .collect(Collectors.toList());

            if (!brandIds.isEmpty()) {
                List<Brand> brands = brandRepository.findSubscribedBrandsByCondition(
                    query.getKeyword(), brandIds);
                data.put("brands", brands);
            } else {
                data.put("brands", Collections.emptyList());
            }

            return ServiceResponse.success(data);

        } catch (Exception e) {
            return ServiceResponse.fail("查詢追蹤項目發生異常: " + e.getMessage());
        }
    }


 

}
