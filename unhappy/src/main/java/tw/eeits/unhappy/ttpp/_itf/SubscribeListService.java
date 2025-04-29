package tw.eeits.unhappy.ttpp._itf;

import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;

public interface SubscribeListService {

    // 建立相關==========================================================
    /**
     * @brief               追蹤/取消追蹤 商品/廠商
     * @param subscribelist 用戶的追蹤清單
     * @return              ServiceResponse封包
     */
    ServiceResponse<SubscribeList> subscribeSwitch(SubscribeList subscribelist);
    // 建立相關==========================================================




}
