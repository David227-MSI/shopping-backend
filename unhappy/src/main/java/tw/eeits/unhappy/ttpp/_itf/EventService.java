package tw.eeits.unhappy.ttpp._itf;

import java.util.List;

import tw.eeits.unhappy.ttpp.event.model.Event;
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
     * @brief       為活動加入圖片/影片媒材
     * @param event 連結的活動
     * @param media 插入的媒體
     * @return      加入成功: True / 加入失敗: False
     */
    Boolean addMediaToEvent(Event event, EventMedia media);

    /**
     * @brief       為活動加入需要的獎品
     * @param event 活動物件
     * @param prize 活動需要的獎品
     * @return      建立成功: True / 建立失敗: False
     */
    Boolean addEventPrize(Event event, EventPrize prize);

    /**
     * @brief        用戶登記為活動參加者
     * @param userId 參加者
     * @param event  登記的活動
     * @param prize  登記的活動獎品
     * @return       登記成功: True / 登記失敗: False
     */
    Boolean attendEvent(Integer userId, Event event, EventPrize prize);
    // 建立活動相關======================================================






    // 用戶查詢相關======================================================
    /**
     * @brief        用戶查詢自己參加活動的次數
     * @param uesrId 查詢的用戶
     * @param event  查詢的活動
     * @return       參加次數
     */
    Integer countAttendTimes(Integer uesrId, Event event);

    /**
     * @brief       查詢活動獎品還剩多少名額
     * @param prize 要查詢的活動獎品 
     * @return      剩餘名額數
     */
    Integer getRemainingSlot(EventPrize prize);

    /**
     * @brief        用戶查詢已登記參加的活動
     * @param userId 用戶
     * @return       登記參加的活動清單
     */
    List<Event> findRegisteredEvent(Integer userId);
    // 用戶查詢相關======================================================







}
