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
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.service.ProductService;
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
@RequestMapping("/api/user/events")
@RequiredArgsConstructor
public class EventUserController {
    private final EventService eventService;
    private final CouponService couponService;
    private final UserMemberService userMemberService;
    private final ProductService productService;
    private final Validator validator;


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
