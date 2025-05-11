package tw.eeits.unhappy.ttpp.event.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.event.dto.EventParticipantRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventQuery;
import tw.eeits.unhappy.ttpp.event.dto.EventRequest;
import tw.eeits.unhappy.ttpp.event.enums.ParticipateStatus;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventParticipant;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.event.repository.EventParticipantRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventPrizeRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;
import tw.eeits.unhappy.ttpp.media.repository.EventMediaRepository;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventPrizeRepository prizeRepository;
    private final EventMediaRepository mediaRepository;
    private final EventParticipantRepository participantRepository;
    private final UserMemberRepository userMemberRepository;
    private final OrderRepository orderRepository;
    private final Validator validator;


    // =================================================================
    // 建立活動相關======================================================
    // =================================================================
    @Override
    public ServiceResponse<Event> createEvent(Event event) {

        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if(event == null) {
            ec.add("輸入活動為null");
        } else {
            ec.validate(event, validator);
        }

        // service logic
        if(event.getStartTime().isBefore(LocalDateTime.now())) {
            ec.add("活動開始時間不能早於現在時間");
        } 
        if(event.getAnnounceTime().isBefore(LocalDateTime.now())) {
            ec.add("活動宣告時間不能早於現在時間");
        }
        if(event.getAnnounceTime().isAfter(event.getStartTime())) {
            ec.add("活動宣告時間不能晚於活動開始時間");
        }

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            Event savedEntry = eventRepository.save(event);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("建立活動錯誤: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<EventMedia> addMediaToEvent(
        Event event, 
        MultipartFile mediaData, 
        MediaType mediaType
    ) throws IOException {
        
        ErrorCollector ec = new ErrorCollector();

        if(event == null) {ec.add("找不到優惠券模板");}
        
        EventMedia newEntry = EventMedia.builder()
                .event(event)
                .mediaType(mediaType)
                .mediaData(mediaData.getBytes())
                .build();
        try {
            EventMedia savedEntry = mediaRepository.save(newEntry);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("圖片添加異常: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<EventPrize> addEventPrize(EventPrize prize) {

        ErrorCollector ec = new ErrorCollector();

        if(prize == null) {
            ec.add("輸入獎品為 null");
        } else {
            ec.validate(prize, validator);
        }

        // service logic
        BigDecimal winRate = prize.getWinRate();
        if (winRate.compareTo(BigDecimal.ZERO) < 0 || winRate.compareTo(BigDecimal.ONE) > 0) {
            ec.add("winRate: winRate 必須在 0.0 到 1.0 之間");
        }



        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            EventPrize savedEntry = prizeRepository.save(prize);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("建立獎品錯誤: " + e);
        }
    }
    // =================================================================
    // 建立活動相關======================================================
    // =================================================================






    // =================================================================
    // 修改相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<Event> modifyEvent(
        Integer eventId, 
        EventRequest request
    ) {

        ErrorCollector ec = new ErrorCollector();

        Event foundEvent = eventRepository.findById(eventId).orElse(null);
        if (foundEvent == null) {
            ec.add("找不到相關活動");
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        if (request == null) {
            ec.add("請輸入修改資訊");
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        LocalDateTime newStartTime = request.getStartTime() != null ? request.getStartTime() : foundEvent.getStartTime();
        LocalDateTime newAnnounceTime = request.getAnnounceTime() != null ? request.getAnnounceTime() : foundEvent.getAnnounceTime();
        LocalDateTime newEndTime = request.getEndTime() != null ? request.getEndTime() : foundEvent.getEndTime();

        if (newStartTime.isBefore(LocalDateTime.now())) {
            ec.add("活動開始時間不能早於現在時間");
        }
        if (newAnnounceTime.isBefore(LocalDateTime.now())) {
            ec.add("活動宣告時間不能早於現在時間");
        }
        if (newAnnounceTime.isAfter(newStartTime)) {
            ec.add("活動宣告時間不能晚於活動開始時間");
        }
        if (newEndTime.isBefore(newStartTime)) {
            ec.add("活動結束時間不能早於開始時間");
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }


        // service operation
        // edit event
        if (request.getEventName() != null) {
            foundEvent.setEventName(request.getEventName());
        }
        if (request.getMinSpend() != null) {
            foundEvent.setMinSpend(request.getMinSpend());
        }
        if (request.getMaxEntries() != null) {
            foundEvent.setMaxEntries(request.getMaxEntries());
        }
        if (request.getStartTime() != null) {
            foundEvent.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            foundEvent.setEndTime(request.getEndTime());
        }
        if (request.getAnnounceTime() != null) {
            foundEvent.setAnnounceTime(request.getAnnounceTime());
        }
        if (request.getEstablishedBy() != null) {
            foundEvent.setEstablishedBy(request.getEstablishedBy());
        }

        // edit media
        EventMedia foundmedia = foundEvent.getEventMedia();
        if (foundmedia == null) {
            ec.add("活動缺少關聯的媒體資料");
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        if (request.getMediaType() != null) {
            foundmedia.setMediaType(request.getMediaType());
        }
        if (request.getMediaData() != null) {
            MultipartFile file = request.getMediaData();
            if (file.isEmpty()) {
                ec.add("上傳的檔案為空");
            } else if (file.getSize() > 5 * 1024 * 1024) { // 限制 5MB
                ec.add("媒體檔案大小超過限制（5MB）");
            } else {
                try {
                    foundmedia.setMediaData(file.getBytes());
                } catch (IOException e) {
                    ec.add("處理媒體檔案時發生錯誤");
                }
            }
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // save 
        try {
            Event savedEvent = eventRepository.save(foundEvent);
            mediaRepository.save(foundmedia);
            return ServiceResponse.success(savedEvent);
        } catch (DataAccessException e) {
            return ServiceResponse.fail("資料庫操作失敗");
        } catch (Exception e) {
            return ServiceResponse.fail("修改活動時發生未知錯誤");
        }
    }

    // =================================================================
    // 修改相關==========================================================
    // =================================================================



    // =================================================================
    // 刪除相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<Boolean> deleteEventById(Integer id) {
        try {
            eventRepository.deleteById(id);
            return ServiceResponse.success(true);
        } catch (Exception e) {
            return ServiceResponse.fail("刪除活動發生異常: " + e);
        }
    }

    @Override
    public ServiceResponse<Boolean> deletePrizeById(Integer id) {
        try {
            prizeRepository.deleteById(id);
            return ServiceResponse.success(true);
        } catch (Exception e) {
            return ServiceResponse.fail("刪除活動獎品發生異常: " + e);
        }
    }
    // =================================================================
    // 刪除相關==========================================================
    // =================================================================





    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    @Override
    public Event findEventById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public ServiceResponse<List<EventPrize>> findAllPrizeByEventId(
        Integer eventId
    ) {
        try {
            List<EventPrize> prizeList = prizeRepository.findByEventId(eventId);
            return ServiceResponse.success(prizeList);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢活動獎品發生異常: " + e);
        }
    }

    @Override
    public EventPrize findPrizeById(Integer id) {
        return prizeRepository.findById(id).orElse(null);
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================














    // =================================================================
    // 條件查詢相關======================================================
    // =================================================================
    @Override
    public ServiceResponse<List<Event>> findEventByCriteria(EventQuery query) {
        
        // service operation
        try {
            List<Event> res = eventRepository.findAll(EventRepository.byEventCriteria(query));
            return ServiceResponse.success(res);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢發生異常: " + e.getMessage());
        }
    }



    @Override
    public ServiceResponse<Map<String, Object>> checkEligibility(
        EventParticipantRequest request
    ) {

        ErrorCollector ec = new ErrorCollector();

        // check event and prize
        Event foundEvent = eventRepository.findById(request.getEventId()).orElse(null);
        EventPrize foundPrize = prizeRepository.findById(request.getPrizeId()).orElse(null);
        
        if(foundEvent == null) {ec.add("找不到目標活動");}
        if(foundPrize == null) {ec.add("找不到目標獎品");}

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        // check the total amount user paid
        BigDecimal totalSpend = orderRepository.sumTotalAmountByUserIdAndPaidAtBetween(
                request.getUserId(),
                foundEvent.getStartTime(),
                foundEvent.getEndTime()
        );
        if (totalSpend == null) totalSpend = BigDecimal.ZERO;

        // compute max allow entries
        BigDecimal minSpend = foundEvent.getMinSpend();
        int maxAllowedEntries = totalSpend.divide(minSpend, 0, RoundingMode.FLOOR).intValue();

        // check participated times
        int participatedTimes = participantRepository.countByUserMemberIdAndEventId(
                request.getUserId(),
                request.getEventId()
        );

        int remainingEntries = Math.max(0, maxAllowedEntries - participatedTimes);

        // compute how much more amount is needed to get next entry
        BigDecimal spentInCurrentEntry = totalSpend.remainder(minSpend);
        BigDecimal amountToNextEntry = (spentInCurrentEntry.compareTo(BigDecimal.ZERO) == 0 && remainingEntries > 0)
                ? BigDecimal.ZERO
                : minSpend.subtract(spentInCurrentEntry);

        // check eligible
        boolean eligible = remainingEntries > 0;

        Map<String, Object> data = new HashMap<>();
        data.put("eligible", eligible);
        data.put("maxAllowedEntries", maxAllowedEntries);
        data.put("participatedTimes", participatedTimes);
        data.put("remainingEntries", remainingEntries);
        data.put("totalSpend", totalSpend);
        data.put("amountToNextEntry", amountToNextEntry);

        return ServiceResponse.success(data);

    }
    // =================================================================
    // 條件查詢相關======================================================
    // =================================================================






















    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @Override
    public ServiceResponse<Map<String, Object>> countUserEntries(Integer userId, Integer eventId) {

        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = null;
        Event foundEvent = null;

        // check input and verify datatype
        if(userId == null) {
            ec.add("請輸入用戶ID");
        } else {
            foundUser = userMemberRepository.findById(userId).orElse(null);
            if(foundUser == null) {ec.add("找不到查詢用戶");}
        }
        if(eventId == null) {
            ec.add("請輸入活動ID");
        } else {
            foundEvent = eventRepository.findById(eventId).orElse(null);
            if(foundEvent == null) {ec.add("找不到查詢活動");}
        }

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            Integer participationCount = participantRepository.countByUserMemberIdAndEventId(userId, eventId);    
            
            // pick up return data
            Map<String, Object> data = new HashMap<>();
            data.put("eventName", foundEvent.getEventName());
            data.put("userEmail", foundUser.getEmail());
            data.put("userId", userId);
            data.put("count", participationCount);

            return ServiceResponse.success(data);

        } catch (Exception e) {
            return ServiceResponse.fail("查詢參加次數異常: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<Map<String, Object>> attendEvent(EventParticipantRequest request) {
        
        ErrorCollector ec = new ErrorCollector();

        Event foundEvent = eventRepository.findById(request.getEventId()).orElse(null);
        EventPrize foundPrize = prizeRepository.findById(request.getPrizeId()).orElse(null);
        UserMember foundUser = userMemberRepository.findById(request.getUserId()).orElse(null);

        if (foundEvent == null) ec.add("找不到目標活動");
        if (foundPrize == null) ec.add("找不到目標獎品");
        if (foundUser == null) ec.add("找不到目標會員");

        if (foundPrize != null && foundPrize.getRemainingSlots() <= 0) {
            ec.add("獎品已無剩餘名額");
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }


        // service operation
        // compute participatedTimes and maxAllowedEntries
        BigDecimal totalSpend = orderRepository.sumTotalAmountByUserIdAndPaidAtBetween(
                request.getUserId(), foundEvent.getStartTime(), foundEvent.getEndTime());
        if (totalSpend == null) totalSpend = BigDecimal.ZERO;

        BigDecimal minSpend = foundEvent.getMinSpend();
        int maxAllowedEntries = totalSpend.divide(minSpend, 0, RoundingMode.FLOOR).intValue();

        int participatedTimes = participantRepository.countByUserMemberIdAndEventId(
                request.getUserId(), request.getEventId());

        if (participatedTimes >= maxAllowedEntries) {
            return ServiceResponse.fail("已達可參加次數上限");
        }

        // lottery process
        boolean isWinner = Math.random() < foundPrize.getWinRate().doubleValue();
        ParticipateStatus finalStatus = isWinner ? ParticipateStatus.WINNER : ParticipateStatus.LOST;

        // record
        EventParticipant savedEntry = EventParticipant.builder()
                .event(foundEvent)
                .eventPrize(foundPrize)
                .userMember(foundUser)
                .participateStatus(finalStatus)
                .build();

        participantRepository.save(savedEntry);

        // 更新剩餘名額（只有中獎才扣）
        if (isWinner) {
            foundPrize.setRemainingSlots(foundPrize.getRemainingSlots() - 1);
            prizeRepository.save(foundPrize);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("participantId", savedEntry.getId());
        data.put("status", finalStatus);
        data.put("prizeTitle", foundPrize.getTitle());

        return ServiceResponse.success(data);
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================

}
