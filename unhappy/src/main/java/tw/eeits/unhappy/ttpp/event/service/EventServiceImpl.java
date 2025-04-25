package tw.eeits.unhappy.ttpp.event.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.EventService;
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
    public Event createEvent(Event event) {

        if(event == null) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            return null;
        }
        System.out.println(3);
        // create event
        try {
            return eventRepository.save(event);
        } catch (Exception e) {
            System.out.println("建立活動錯誤: " + e);
            return null;
        }
    }

    @Override
    public EventMedia addMediaToEvent(EventMedia media) {
        if(media == null) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<EventMedia>> violations = validator.validate(media);
        if (!violations.isEmpty()) {
            return null;
        }

        // create event media
        try {
            return mediaRepository.save(media);
        } catch (Exception e) {
            System.out.println("建立活動媒體錯誤: " + e);
            return null;
        }

    }

    @Override
    public EventPrize addEventPrize(EventPrize prize) {
        if(prize == null) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<EventPrize>> violations = validator.validate(prize);
        if (!violations.isEmpty()) {
            return null;
        }

        // create event prize
        try {
            return prizeRepository.save(prize);
        } catch (Exception e) {
            System.out.println("建立獎品錯誤: " + e);
            return null;
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
    public EventParticipant attendEvent(EventParticipant participant) {
        if(participant == null ||
            participant.getEvent() == null || 
            participant.getEventPrize() == null
        ) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<EventParticipant>> violations = validator.validate(participant);
        if (!violations.isEmpty()) {
            return null;
        }

        // check event maxEntries
        // Integer maxEntries = participant.getEvent().getMaxEntries();

        // create event participant
        try {
            return participantRepository.save(participant);
        } catch (Exception e) {
            System.out.println("建立活動參加者錯誤: " + e);
            return null;
        }
    }


    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================




}
