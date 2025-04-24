package tw.eeits.unhappy.ttpp._itf;

import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;

public interface SubscribeListService {

    // 追蹤商品相關======================================================
    /**
     * @brief               追蹤商品
     * @param subscribelist 用戶的追蹤清單
     * @return              追蹤成功: True / 追蹤失敗: False
     */
    Boolean SubscribeProduct(SubscribeList subscribelist);
    /**
     * @brief               追蹤廠商
     * @param subscribelist 用戶的追蹤清單
     * @return              追蹤成功: True / 追蹤失敗: False
     */
    Boolean SubscribeBrand(SubscribeList subscribelist);
    /**
     * @brief               取消追蹤商品
     * @param subscribelist 用戶的追蹤清單
     * @return              取消成功: True / 取消失敗: False
     */
    Boolean unSubscribeProduct(SubscribeList subscribelist);
    /**
     * @brief               取消追蹤廠商
     * @param subscribelist 用戶的追蹤清單
     * @return              取消成功: True / 取消失敗: False
     */
    Boolean unSubscribeBrand(SubscribeList subscribelist);
    // 追蹤商品相關======================================================




}
