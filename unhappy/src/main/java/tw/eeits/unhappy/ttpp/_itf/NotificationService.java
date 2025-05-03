package tw.eeits.unhappy.ttpp._itf;

import java.util.List;

import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

public interface NotificationService {

    // 建立相關==========================================================
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
    // 建立相關==========================================================




    // 修改相關==========================================================
    /**
     * @brief    將用戶通知訊息標記已讀
     * @param id 通知訊息ID
     * @return   ServiceResponse封包
     */
    ServiceResponse<NotificationPublished> markNotificationAsRead(Integer id);


    /**
     * @brief            將用戶的所有通知訊息標記已讀
     * @param userMember 用戶
     * @return           ServiceResponse封包
     */
    ServiceResponse<Integer> markAllAsReadByUserMember(UserMember userMember);
    // 修改相關==========================================================


    // 刪除相關==========================================================

    /**
     * @brief    用戶刪除自己的通知訊息
     * @param id 通知訊息ID
     * @return   ServiceResponse封包
     */
    ServiceResponse<Boolean> deleteNotificationById(Integer id);

    /**
     * @brief            用戶刪除自己的所有通知訊息
     * @param userMember 用戶
     * @return           ServiceResponse封包
     */
    ServiceResponse<Boolean> deleteNotificationByUserMember(UserMember userMember);
    // 刪除相關==========================================================
    

    



    // 基本查詢相關======================================================
    /**
     * @brief    以ID查詢通知訊息的模板
     * @param id 模板 ID
     * @return   查詢成功: 模板 / 查詢失敗: Null
     */
    NotificationTemplate findTemplateById(Integer id);

    /**
     * @brief    以ID查詢用戶通知訊息
     * @param id 用戶通知訊息 ID
     * @return   ServiceResponse封包
     */
    ServiceResponse<NotificationPublished> findNotificationById(Integer id);
    // 基本查詢相關======================================================
    
    
    
    // 條件查詢相關======================================================
    /**
     * @brief       根據條件查詢通知訊息模板
     * @param query 查詢條件
     * @return      符合條件的通知訊息模板
     */
    ServiceResponse<List<NotificationTemplate>> findTemplatesByCriteria(NotificationQuery query);
    // 條件查詢相關======================================================






    // 用戶查詢相關======================================================
    /**
     * @brief       根據條件查詢用戶的通知訊息
     * @param query 查詢條件
     * @return      符合條件的通知訊息清單
     */
    ServiceResponse<List<NotificationPublished>> findNotificationsByCriteria(NotificationQuery query);
    // 用戶查詢相關======================================================


}
