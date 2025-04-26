package tw.eeits.unhappy.ttpp.notification.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.SubscribeListService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;
import tw.eeits.unhappy.ttpp.notification.repository.SubscribeListRepository;

@Service
@RequiredArgsConstructor
public class SubScribeServiceImpl implements SubscribeListService {

    private final SubscribeListRepository subscribeListRepository;

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
            SubscribeList existing = subscribeListRepository.findByUserMemberAndItemTypeAndItemId(
                subscribe.getUserMember(), 
                subscribe.getItemType(), 
                subscribe.getItemId()
            );

            if (existing != null) {
                // if record exists, switch subscribe status
                existing.setIsSubscribing(!existing.getIsSubscribing());
                SubscribeList updated = subscribeListRepository.save(existing);
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

 

}
