package tw.eeits.unhappy.ttpp._itf;

import java.util.List;

import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

public interface NotificationService {

    // 建立通知相關======================================================
    /**
     * @brief          建立通知訊息的模板
     * @param template 模板物件
     * @return         ServiceResponse封包
     */
    ServiceResponse<NotificationTemplate> createTemplate(NotificationTemplate template);

    /**
     * @brief                        發送通知訊息給用戶
     * @param notificationPublished  預計要發送的訊息
     * @return                       ServiceResponse封包
     */
    ServiceResponse<NotificationPublished> publishNotification(NotificationPublished notificationPublished);
    // 建立通知相關======================================================

    



    // 基本查詢相關======================================================
    /**
     * @brief    以ID查詢通知訊息的模板
     * @param id 模板 ID
     * @return   查詢成功: 模板 / 查詢失敗: Null
     */
    NotificationTemplate findTemplateById(Integer id);



    /**
     * @brief       根據條件查詢通知訊息模板
     * @param query 查詢條件
     * @return      符合條件的通知訊息模板
     */
    public List<NotificationTemplate> findTemplatesByCriteria(NotificationQuery query);
    // 基本查詢相關======================================================

    // 用戶查詢相關======================================================
    /**
     * @brief       根據條件查詢用戶的通知訊息
     * @param query 查詢條件
     * @return      符合條件的通知訊息清單
     */
    public List<NotificationPublished> findNotificationsByCriteria(NotificationQuery query);
    // 用戶查詢相關======================================================







//     /**
//      * @brief            為訂閱用戶儲存促銷或追蹤通知訊息
//      * @param itemId     商品/廠商 ID
//      * @param itemType   種類: 商品/廠商
//      * @param templateId 模板 ID
//      * @param expiredAt  過期時間
//      * @return           儲存的通知列表
//      */
//     List<NotificationPublished> savePromotionOrWishlistNotifications(
//         Integer itemId, 
//         ItemType itemType,
//         Integer templateId, 
//         LocalDateTime expiredAt
//     );

//     /**
//      * @brief            為訂閱用戶生成定期通知
//      * @param templateId 模板 ID
//      * @param expiredAt  過期時間
//      * @return           儲存的通知列表
//      */
//     List<NotificationPublished> saveSubscriptionNotifications(
//         Integer templateId, 
//         LocalDateTime expiredAt
//     );

    // /**
    //  * @brief        用戶查詢收到的所有通知
    //  * @param userId 用戶  // to be arranged after user fk created
    //  * @return       通知列表
    //  */
    // List<NotificationPublished> findNotificationsByUser(Integer userId); // to be arranged after user fk created
}
