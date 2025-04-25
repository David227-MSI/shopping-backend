package tw.eeits.unhappy.ttpp._itf;


import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventParticipant;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;

public interface EventService {


    // 建立活動相關======================================================
    /**
     * @brief       建立新活動
     * @param event 活動物件
     * @return      建立成功: 活動物件 / 建立失敗: Null
     */
    Event createEvent(Event event);

    /**
     * @brief       為活動建立圖片/影片媒材
     * @param media 插入的媒體
     * @return      建立成功: 媒體物件 / 建立失敗: Null
     */
    EventMedia addMediaToEvent(EventMedia media);

    /**
     * @brief       為活動建立需要的獎品
     * @param prize 活動需要的獎品
     * @return      建立成功: 獎品物件 / 建立失敗: Null
     */
    EventPrize addEventPrize(EventPrize prize);
    // 建立活動相關======================================================




    // 基本查詢相關======================================================
    /**
     * @brief    用ID查詢活動
     * @param id 活動 ID
     * @return   查詢成功: 活動物件 / 查詢失敗: Null
     */
    Event findEventById(Integer id);

    /**
     * @brief    用ID查詢活動獎品
     * @param id 活動獎品 ID
     * @return   查詢成功: 活動獎品物件 / 查詢失敗: Null
     */
    EventPrize findPrizeById(Integer id);
    // 基本查詢相關======================================================




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
     * @brief             建立活動參加者
     * @param participant 登記的活動參加者
     * @return            登記成功: 參加者物件 / 登記失敗: Null
     */
    EventParticipant attendEvent(EventParticipant participant);
    // 用戶操作相關======================================================



}
