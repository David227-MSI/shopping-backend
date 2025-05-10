package tw.eeits.unhappy.ttpp.event.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.service.ProductService;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.event.dto.EventPrizeRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventQuery;
import tw.eeits.unhappy.ttpp.event.dto.EventRequest;
import tw.eeits.unhappy.ttpp.event.enums.EventStatus;
import tw.eeits.unhappy.ttpp.event.enums.PrizeType;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.media.dto.MediaRequest;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;
    private final CouponService couponService;
    private final ProductService productService;
    private final Validator validator;

    // =================================================================
    // 建立活動相關======================================================
    // =================================================================
    @PostMapping(value = "/createEvent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiRes<Map<String, Object>>> createEvent(
        @ModelAttribute EventRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);

        if(request.getMediaData() == null) {
            ec.add("請上傳活動圖片");
        }
        
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
        ServiceResponse<Event> resEvent = eventService.createEvent(newEntry);

        if (!resEvent.isSuccess()) {
            ec.add(resEvent.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(resEvent.getMessage()));
        }


        Event savedEvent = resEvent.getData();
        ServiceResponse<EventMedia> resMedia = null;
        try {
            resMedia = eventService.addMediaToEvent(
                savedEvent, 
                request.getMediaData(), 
                request.getMediaType()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
            .body(ResponseFactory.fail("上傳圖片發生異常: " + e.getMessage()));
        }

        // pick up response data
        EventMedia savedMedia = resMedia.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEvent.getId());
        data.put("eventName", savedEvent.getEventName());
        data.put("announceTime", savedEvent.getAnnounceTime());
        data.put("startTime", savedEvent.getStartTime());
        data.put("EstablishedBy", savedEvent.getEstablishedBy());
        data.put("mediaType", savedMedia.getMediaType());
        data.put("mediaData", savedMedia.getMediaData());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @PostMapping("/addMedia")
    public ResponseEntity<ApiRes<Map<String, Object>>> addMediaToEvent(
        @RequestBody MediaRequest request) throws IOException {

        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);

        // check foreign key
        Event foundEvent = eventService.findEventById(request.getId());
        if(foundEvent == null) {ec.add("找不到相關活動");}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }
        
        // call service
        ServiceResponse<EventMedia> res = eventService.addMediaToEvent(
            foundEvent,
            request.getMediaData(),
            request.getMediaType()
        );

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
            Product foundItem = productService.getProductById(request.getItemId()).orElse(null);
            if(foundItem == null) {ec.add("找不到套用的商品");}
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
    // 修改相關==========================================================
    // =================================================================

    @PutMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<ApiRes<Map<String, Object>>> modifyEvent(
        @PathVariable Integer eventId, 
        @ModelAttribute EventRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);
        
        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<Event> resEvent = eventService.modifyEvent(eventId, request);

        if (!resEvent.isSuccess()) {
            ec.add(resEvent.getMessage());
            return ResponseEntity.badRequest()
                .body(ResponseFactory.fail(resEvent.getMessage()));
        }

        Event savedEvent = resEvent.getData();

        // pick up response data
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEvent.getId());
        data.put("eventName", savedEvent.getEventName());
        data.put("announceTime", savedEvent.getAnnounceTime());
        data.put("startTime", savedEvent.getStartTime());
        data.put("EstablishedBy", savedEvent.getEstablishedBy());
        data.put("mediaType", savedEvent.getEventMedia().getMediaType());
        data.put("mediaData", savedEvent.getEventMedia().getMediaData());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    // =================================================================
    // 修改相關==========================================================
    // =================================================================














    // =================================================================
    // 刪除相關==========================================================
    // =================================================================
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiRes<Boolean>> deleteEventById(
        @PathVariable Integer eventId
    ) {
        ServiceResponse<Boolean> res = eventService.deleteEventById(eventId);

        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        return ResponseEntity.ok(ResponseFactory.success(res.getData()));
    }
    // =================================================================
    // 刪除相關==========================================================
    // =================================================================




















    // =================================================================
    // 查詢所有活動======================================================
    // =================================================================
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiRes<Map<String, Object>>> getEventById(
        @PathVariable Integer eventId
    ) {
        
        Event foundEvent = eventService.findEventById(eventId);
        if(foundEvent == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail("找不到相關活動"));
        }

        EventMedia foundMedia = foundEvent.getEventMedia();
        Map<String, Object> data = new HashMap<>();
        data.put("id", foundEvent.getId());
        data.put("eventName", foundEvent.getEventName());
        data.put("minSpend", foundEvent.getMinSpend());
        data.put("maxEntries", foundEvent.getMaxEntries());
        data.put("announceTime", foundEvent.getAnnounceTime());
        data.put("startTime", foundEvent.getStartTime());
        data.put("endTime", foundEvent.getEndTime());
        data.put("EstablishedBy", foundEvent.getEstablishedBy());
        if(foundMedia != null) {
            data.put("mediaType", foundMedia.getMediaType());
            data.put("mediaData", foundMedia.getMediaData());
        }

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    
    
    
    
    
    
    @PostMapping("/findAll")
    public ResponseEntity<ApiRes<Map<String, Object>>> findAllEvents(
        @RequestBody EventQuery query) {

        // call service
        ServiceResponse<List<Event>> res = eventService.findEventByCriteria(query);
        
        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        List<Event> foundData = res.getData();
        List<Map<String, Object>> eventList = foundData.stream().map(event -> {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", event.getId());
            mp.put("eventName", event.getEventName());
            mp.put("startTime", event.getStartTime());
            mp.put("endTime", event.getEndTime());
            mp.put("announceTime", event.getAnnounceTime());
            mp.put("eventPrizeList", event.getEventPrize());
            mp.put("eventMedia", event.getEventMedia());
            return mp;
        }).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("eventList", eventList);

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    // =================================================================
    // 查詢所有活動======================================================
    // =================================================================





}
