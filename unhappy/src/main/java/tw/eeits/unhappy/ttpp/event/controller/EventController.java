package tw.eeits.unhappy.ttpp.event.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberService;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
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


@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final CouponService couponService;
    private final UserMemberService userMemberService;
    private final Validator validator;



    // =================================================================
    // 建立活動相關======================================================
    // =================================================================
    @PostMapping("/createEvent")
    public ResponseEntity<ApiRes<Map<String, Object>>> createEvent(
        @RequestBody EventRequest request) {

        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);
        
        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
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

        // call service
        ServiceResponse<Event> res = eventService.createEvent(newEntry);

        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        Event savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("eventName", savedEntry.getEventName());
        data.put("announceTime", savedEntry.getAnnounceTime());
        data.put("startTime", savedEntry.getStartTime());
        data.put("EstablishedBy", savedEntry.getEstablishedBy());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @PostMapping("/addMedia")
    public ResponseEntity<ApiRes<Map<String, Object>>> addMediaToEvent(
        @RequestBody EventMediaRequest request) {

        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);

        // check foreign key
        Event foundEvent = eventService.findEventById(request.getEventId());
        if(foundEvent == null) {ec.add("找不到相關活動");}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
        EventMedia newEntry = EventMedia.builder()
                .event(foundEvent)
                .mediaData(request.getMediaData())
                .mediaType(request.getMediaType())
                .build();
        
        // call service
        ServiceResponse<EventMedia> res = eventService.addMediaToEvent(newEntry);

        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        EventMedia savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("mediaType", savedEntry.getMediaType());
        data.put("eventName", savedEntry.getEvent().getEventName());
        
        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @PostMapping("/addPrize")
    public ResponseEntity<ApiRes<Map<String, Object>>> addPrizeToEvent(
        @RequestBody EventPrizeRequest request) {
        
        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);

        BigDecimal winRate = BigDecimal.ZERO;
        try {
            winRate = new BigDecimal(request.getWinRate());
        } catch (NumberFormatException e) {
            ec.add("winRate: 中講機率格式錯誤");
        }

        // check foreign key
        Event foundEvent = eventService.findEventById(request.getEventId());
        if(foundEvent == null) {ec.add("找不到套用的活動");}

        // check itemType
        if(request.getItemType() == PrizeType.COUPON_TEMPLATE) {
            CouponTemplate foundItem = couponService.findTemplateById(request.getItemId());
            if(foundItem == null) {ec.add("找不到套用的折價券模板");}
        } else if(request.getItemType() == PrizeType.PRODUCT) {
            // Product foundItem = productService.findProductById(request.getItemId());
            // if(foundItem == null) {ec.add("找不到套用的折價券模板");}
        }

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
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
        
        //call service
        ServiceResponse<EventPrize> res = eventService.addEventPrize(newEntry);

        if(!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // pick up response data
        EventPrize savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("title", savedEntry.getTitle());
        data.put("itemType", savedEntry.getItemType());
        data.put("itemId", savedEntry.getItemId());
        data.put("quantity", savedEntry.getQuantity());
        data.put("totalSlots", savedEntry.getTotalSlots());
        data.put("winRate", savedEntry.getWinRate());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    
    // =================================================================
    // 建立活動相關======================================================
    // =================================================================



    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/user/attendEvent")
    public ResponseEntity<ApiRes<Map<String, Object>>> attendEvent(
        @RequestBody EventParticipantRequest request) {

        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);
        
        // check foreign key
        UserMember foundUser = userMemberService.findUserById(request.getUserId());
        Event foundEvent = eventService.findEventById(request.getEventId());
        EventPrize foundPrize = eventService.findPrizeById(request.getPrizeId());

        if(foundUser == null) {ec.add("找不到參加用戶資訊");}
        if(foundEvent == null) {ec.add("找不到參加的活動");}
        if(foundPrize == null) {ec.add("找不到參加的活動獎品");}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
        EventParticipant newEntry = EventParticipant.builder()
                .userMember(foundUser)
                .event(foundEvent)
                .eventPrize(foundPrize)
                .participateStatus(ParticipateStatus.REGISTERED)
                .build();

        // call service
        ServiceResponse<EventParticipant> res = eventService.attendEvent(newEntry);

        if(!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // pick up response data
        EventParticipant savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("userId", savedEntry.getUserMember().getId());
        data.put("eventName", savedEntry.getEvent().getEventName());
        data.put("eventPrize", savedEntry.getEventPrize());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================








}
