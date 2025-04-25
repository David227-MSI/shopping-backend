package tw.eeits.unhappy.ttpp.event.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.event.dto.EventParticipantRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventPrizeRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventRequest;
import tw.eeits.unhappy.ttpp.event.enums.EventStatus;
import tw.eeits.unhappy.ttpp.event.enums.ParticipateStatus;
import tw.eeits.unhappy.ttpp.event.enums.PrizeType;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventParticipant;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.media.dto.EventMediaRequest;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;

import java.math.BigDecimal;
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
    private final CouponService couponService;
    private final Validator validator;



    // =================================================================
    // 建立活動相關======================================================
    // =================================================================
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

    @PostMapping("/addPrize")
    public ResponseEntity<ApiRes<EventPrize>> addPrizeToEvent(
        @RequestBody EventPrizeRequest request) {
        
        // verify data type
        Set<ConstraintViolation<EventPrizeRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
        }

        BigDecimal winRate;
        try {
            winRate = new BigDecimal(request.getWinRate());
            if (winRate.compareTo(BigDecimal.ZERO) < 0 || winRate.compareTo(BigDecimal.ONE) > 0) {
                return ResponseEntity.badRequest().body(ResponseFactory.fail("winRate: winRate 必須在 0.0 到 1.0 之間"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail("winRate: 無效的數字格式"));
        }

        // check event
        Event foundEvent = eventService.findEventById(request.getEventId());
        if(foundEvent == null) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到套用的活動"));
        }

        // check itemType
        if(request.getItemType() == PrizeType.COUPON_TEMPLATE) {
            CouponTemplate foundItem = couponService.findTemplateById(request.getItemId());
            if(foundItem == null) {
                return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到套用的折價券模板"));
            }
        } else if(request.getItemType() == PrizeType.PRODUCT) {
            // Product foundItem = productService.findProductById(request.getItemId());
            // if(foundItem == null) {
            //     return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到套用的折價券模板"));
            // }
        }

        EventPrize newEntry = EventPrize.builder()
                .event(foundEvent)
                .itemId(1)
                .itemType(request.getItemType())
                .quantity(request.getQuantity())
                .winRate(winRate)
                .totalSlots(request.getTotalSlots())
                .remainingSlots(request.getTotalSlots())
                .title(request.getTitle())
                .build();
        EventPrize savedEntry = eventService.addEventPrize(newEntry);

        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }
    
    // =================================================================
    // 建立活動相關======================================================
    // =================================================================



    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/user/attendEvent")
    public ResponseEntity<ApiRes<EventParticipant>> attendEvent(
        @RequestBody EventParticipantRequest request) {

        // verify data type
        Set<ConstraintViolation<EventParticipantRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
        }

        // check user
        
        // check event
        Event foundEvent = eventService.findEventById(request.getEventId());
        if(foundEvent == null) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到套用的活動"));
        }
        // check prize
        EventPrize foundPrize = eventService.findPrizeById(request.getPrizeId());
        if(foundPrize == null) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到套用的活動獎品"));
        }

        EventParticipant newEntry = EventParticipant.builder()
                .userId(request.getUserId())
                .event(foundEvent)
                .eventPrize(foundPrize)
                .participateStatus(ParticipateStatus.REGISTERED)
                .build();

        EventParticipant savedEntry = eventService.attendEvent(newEntry);

        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }
    
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================








}
