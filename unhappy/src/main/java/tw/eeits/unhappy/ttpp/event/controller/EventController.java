package tw.eeits.unhappy.ttpp.event.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponTemplateRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventRequest;
import tw.eeits.unhappy.ttpp.event.enums.EventStatus;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.media.dto.EventMediaRequest;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/app/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final Validator validator;


    @PostMapping("/createEvent")
    public ResponseEntity<ApiRes<Event>> createEvent(
        @RequestBody EventRequest request) {

        // verify data type
        Set<ConstraintViolation<EventRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
        }
        
        if(request.getStartTime().isBefore(LocalDateTime.now()) || 
            request.getAnnounceTime().isBefore(LocalDateTime.now()) ||
            request.getAnnounceTime().isAfter(request.getStartTime())
        ) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail("活動宣告或開始時間錯誤"));
        }

        Event newEntry = Event.builder()
                .eventName(request.getEventName())
                .minSpend(request.getMinSpend())
                .maxEntries(request.getMaxEntries())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .announceTime(request.getAnnounceTime())
                .eventStatus(EventStatus.ANNOUNCED)
                .establishedBy(request.getEstablishedBy())
                .build();
        System.out.println(newEntry);
        Event savedEntry = eventService.createEvent(newEntry);

        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }

    @PostMapping("/addMedia")
    public ResponseEntity<ApiRes<EventMedia>> addMediaToEvent(
        @RequestBody EventMediaRequest request) {
        
        // verify data type
        Set<ConstraintViolation<EventMediaRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
        }

        // check event
        Event foundEvent = eventService.findEventById(request.getEventId());
        if(foundEvent == null) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到相關活動"));
        }

        EventMedia newEntry = EventMedia.builder()
                .event(foundEvent)
                .mediaData(request.getMediaData())
                .mediaType(request.getMediaType())
                .build();
        EventMedia savedEntry = eventService.addMediaToEvent(newEntry);
            
        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }
    








}
