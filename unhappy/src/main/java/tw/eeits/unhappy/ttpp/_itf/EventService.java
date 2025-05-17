package tw.eeits.unhappy.ttpp._itf;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.event.dto.EventParticipantRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventQuery;
import tw.eeits.unhappy.ttpp.event.dto.EventRequest;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;


public interface EventService {

    // 建立活動相關======================================================
    /**
     * @brief       建立新活動
     * @param event 活動物件
     * @return      建立成功: 活動物件 / 建立失敗: Null
     */
    ServiceResponse<Event> createEvent(Event event);

    /**
     * @brief       建立活動用圖片/影片媒材
     * @param media 插入的媒體
     * @return      ServiceResponse封包
     */
    ServiceResponse<EventMedia> addMediaToEvent(
        Event event, 
        MultipartFile mediaData, 
        MediaType mediaType
    ) throws IOException;

    /**
     * @brief       建立活動用的獎品
     * @param prize 活動需要的獎品
     * @return      ServiceResponse封包
     */
    ServiceResponse<EventPrize> addEventPrize(EventPrize prize);
    // 建立活動相關======================================================
    
    
    
    
    // 修改相關==========================================================
    /**
     * @brief         修改活動
     * @param eventId 活動ID
     * @param request 修改的欄位資訊
     * @return        ServiceResponse封包
     */
    ServiceResponse<Event> modifyEvent(Integer eventId, EventRequest request);
    // 修改相關==========================================================






    // 刪除相關==========================================================
    /**
     * @brief    以活動ID刪除指定活動
     * @param id 活動ID
     * @return   ServicePesponse封包
     */
    ServiceResponse<Boolean> deleteEventById(Integer id);

    /**
     * @brief    以獎品ID刪除指定獎品
     * @param id 獎品ID
     * @return   ServicePesponse封包
     */
    ServiceResponse<Boolean> deletePrizeById(Integer id);
    // 刪除相關==========================================================




    // 基本查詢相關======================================================
    /**
     * @brief    用活動ID查詢活動
     * @param id 活動 ID
     * @return   查詢成功: 活動物件 / 查詢失敗: Null
     */
    Event findEventById(Integer id);

    /**
     * @brief         以活動ID查看獎品清單
     * @param eventId 活動ID
     * @return        ServiceResponse封包
     */
    ServiceResponse<List<EventPrize>> findAllPrizeByEventId(Integer eventId);




    /**
     * @brief    用獎品ID查詢活動獎品
     * @param id 活動獎品 ID
     * @return   查詢成功: 活動獎品物件 / 查詢失敗: Null
     */
    EventPrize findPrizeById(Integer id);
    // 基本查詢相關======================================================
    
    
    
    
    
    // 條件查詢相關======================================================
    /**
     * @brief       根據條件查詢活動清單
     * @param query 查詢條件
     * @return      ServiceResponse封包
     */
    ServiceResponse<List<Event>> findEventByCriteria(EventQuery query);






    ServiceResponse<Map<String, Object>> checkEligibility(EventParticipantRequest request);
    // 條件查詢相關======================================================




    // 用戶查詢相關======================================================
    // /**
    //  * @brief        用戶查詢自己參加活動的次數
    //  * @param uesrId 查詢的用戶
    //  * @param event  查詢的活動
    //  * @return       參加次數
    //  */
    // Integer countAttendTimes(Integer uesrId, Event event);

    // /**
    //  * @brief       查詢活動獎品還剩多少名額
    //  * @param prize 要查詢的活動獎品 
    //  * @return      剩餘名額數
    //  */
    // Integer getRemainingSlot(EventPrize prize);

    // 用戶查詢相關======================================================



    // 用戶操作相關======================================================
    /**
     * @brief         查詢用戶已參加特定活動的次數
     * @param userId  查詢的用戶ID
     * @param eventId 查詢的活動ID
     * @return        ServiceResponse封包
     */
    ServiceResponse<Map<String, Object>> countUserEntries(Integer userId, Integer eventId);

    /**
     * @brief             建立活動參加者
     * @param participant 登記的活動參加者
     * @return            ServiceResponse封包
     */
    ServiceResponse<Map<String, Object>> attendEvent(EventParticipantRequest request);
    // 用戶操作相關======================================================

}
