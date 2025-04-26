package tw.eeits.unhappy.ttpp.event.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventParticipant;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.event.repository.EventParticipantRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventPrizeRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;
import tw.eeits.unhappy.ttpp.media.repository.EventMediaRepository;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventPrizeRepository prizeRepository;
    private final EventMediaRepository mediaRepository;
    private final EventParticipantRepository participantRepository;
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
    public ServiceResponse<EventMedia> addMediaToEvent(EventMedia media) {

        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if(media == null) {
            ec.add("輸入媒體為 null");;
        } else {
            ec.validate(media, validator);
        }

        // service logic





        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            EventMedia savedEntry = mediaRepository.save(media);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("建立活動媒體錯誤: " + e.getMessage());
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
    // 基本查詢相關======================================================
    // =================================================================
    @Override
    public Event findEventById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public EventPrize findPrizeById(Integer id) {
        return prizeRepository.findById(id).orElse(null);
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================



    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    // @Override
    // public Integer countUserEntries(Event event) {
    //     return eventRepository.count(event.getUserId());
    // }



    @Override
    public ServiceResponse<EventParticipant> attendEvent(EventParticipant participant) {
        
        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if(participant == null) {
            ec.add("輸入參加者為null");
        } else {
            if(participant.getEvent() == null) {ec.add("輸入活動為 null");} 
            if(participant.getEventPrize() == null) {ec.add("輸入獎品為 null");}
            ec.validate(participant, validator);
        }
        

        // check event maxEntries
        // Integer maxEntries = participant.getEvent().getMaxEntries();




        // service operation
        try {
            EventParticipant savedEntry = participantRepository.save(participant);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("建立活動參加者錯誤: " + e.getMessage());
        }
    }


    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================




}
