package tw.eeits.unhappy.ttpp.event.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.ttpp._itf.EventService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.event.dto.EventParticipantRequest;
import tw.eeits.unhappy.ttpp.event.dto.EventQuery;
import tw.eeits.unhappy.ttpp.event.enums.ParticipateStatus;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventParticipant;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;


@RestController
@RequestMapping("/api/user/events")
@RequiredArgsConstructor
public class EventUserController {
    private final EventService eventService;
    private final UserMemberService userMemberService;
    private final Validator validator;


    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/attendEvent")
    public ResponseEntity<ApiRes<Map<String, Object>>> attendEvent(
        @RequestBody EventParticipantRequest request) {

        ErrorCollector ec = new ErrorCollector();

        // verify data type
        ec.validate(request, validator);
        
        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<Map<String, Object>> res = eventService.attendEvent(request);

        if(!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseFactory.success(res.getData()));
    }
    
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================


    @PostMapping("/query")
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
            mp.put("maxEntries", event.getMaxEntries());
            mp.put("minSpend", event.getMinSpend());
            mp.put("eventPrizeList", event.getEventPrize());
            mp.put("eventMedia", event.getEventMedia());
            return mp;
        }).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("eventList", eventList);

        return ResponseEntity.ok(ResponseFactory.success(data));
    }


    @PostMapping("/checkEligibility")
    public ResponseEntity<ApiRes<Map<String, Object>>> checkEligibility(
        @RequestBody EventParticipantRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        // verify data type
        if (request == null) {ec.add("請求不能為空");} 
        else {ec.validate(request, validator);}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }


        // call service
        ServiceResponse<Map<String, Object>> res = eventService.checkEligibility(request);

        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        return ResponseEntity.ok(ResponseFactory.success(res.getData()));
    }



}
