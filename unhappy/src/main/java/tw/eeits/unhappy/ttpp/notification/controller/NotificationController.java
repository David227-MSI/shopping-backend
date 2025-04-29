package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationPublishRequest;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationTemplateRequest;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserMemberService userMemberService;
    private final Validator validator;

    // =================================================================
    // 建立通知相關======================================================
    // =================================================================
    @PostMapping("/template")
    public ResponseEntity<ApiRes<Map<String, Object>>> createTemplate(
        @RequestBody NotificationTemplateRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        // verify request data
        ec.validate(request, validator);

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
        NotificationTemplate newEntry = NotificationTemplate.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeType(request.getNoticeType())
                .build();

        // call service
        ServiceResponse<NotificationTemplate> res = notificationService.createTemplate(newEntry);
        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }
        
        // pick up response data
        NotificationTemplate savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedEntry.getId());
        data.put("title", savedEntry.getTitle());
        data.put("noticeType", savedEntry.getNoticeType());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    
    @PostMapping("/publish")
    public ResponseEntity<ApiRes<Map<String, Object>>> publishNotification(
        @RequestBody NotificationPublishRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        // verify request data
        ec.validate(request, validator);
        
        // verify foreign key
        NotificationTemplate foundTemplate = notificationService.findTemplateById(request.getTemplateId());
        UserMember foundUser = userMemberService.findUserById(request.getUserId());
        
        if(foundTemplate == null) {ec.add("找不到套用通知訊息模板");}
        if(foundUser == null) {ec.add("找不到目標用戶");}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // transfer data from DTO to Entity
        NotificationPublished notification = NotificationPublished.builder()
            .userMember(foundUser)
            .notificationTemplate(foundTemplate)
            .isRead(false)
            .expiredAt(request.getExpiredAt())
            .build();

        // call service
        ServiceResponse<NotificationPublished> res = notificationService.publishNotification(notification);
        if (!res.isSuccess()) {
            ec.add(res.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // publish notification
        NotificationPublished savedEntry = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", savedEntry.getUserMember().getId());
        data.put("title", savedEntry.getNotificationTemplate().getTitle());
        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    // =================================================================
    // 建立通知相關======================================================
    // =================================================================


    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    @GetMapping("/templates/{id}")
    public ResponseEntity<ApiRes<NotificationTemplate>> findTemplateById(@PathVariable Integer id) {
        NotificationTemplate foundEntry = notificationService.findTemplateById(id);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }

    @PostMapping("/templates/findAll")
    public ResponseEntity<ApiRes<Map<String, Object>>> findAllTemplates(
        @RequestBody NotificationQuery query) {

        // call service
        ServiceResponse<List<NotificationTemplate>> res = notificationService.findTemplatesByCriteria(query);

        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        List<NotificationTemplate> foundData = res.getData();
        List<Map<String, Object>> templateList = foundData.stream().map(template -> {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", template.getId());
            mp.put("title", template.getTitle());
            mp.put("content", template.getContent());
            mp.put("noticeType", template.getNoticeType());
            return mp;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("templateList", templateList);

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================






    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/user/query")
    public ResponseEntity<ApiRes<Map<String, Object>>> findUserNotifications(
        @RequestBody NotificationQuery query) {
        
        // call service
        ServiceResponse<List<NotificationPublished>> res = notificationService.findNotificationsByCriteria(query);

        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        // pick up response data
        List<NotificationPublished> foundData = res.getData();
        List<Map<String, Object>> notificationList = foundData.stream().map(notification -> {
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", notification.getId());
            mp.put("isUsed", notification.getIsRead());
            mp.put("title", notification.getNotificationTemplate().getTitle());
            mp.put("noticeType", notification.getNotificationTemplate().getNoticeType());
            mp.put("createdAt", notification.getCreatedAt());
            mp.put("content", notification.getNotificationTemplate().getContent());
            return mp;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("notificationList", notificationList);

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @GetMapping("/user/notification/{id}")
    public ResponseEntity<ApiRes<Map<String, Object>>> getNotificationById(@PathVariable Integer id) {

        ErrorCollector ec = new ErrorCollector();

        if(id == null) {ec.add("通知訊息 ID 為必要欄位");}

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<NotificationPublished> res = notificationService.findNotificationById(id);
        NotificationPublished foundData = res.getData();
        Map<String, Object> data = new HashMap<>();
        data.put("title", foundData.getNotificationTemplate().getTitle());
        data.put("content", foundData.getNotificationTemplate().getContent());
        data.put("isRead", foundData.getIsRead());
        data.put("noticeType", foundData.getNotificationTemplate().getNoticeType());
        data.put("createdAt", foundData.getCreatedAt());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================






}
