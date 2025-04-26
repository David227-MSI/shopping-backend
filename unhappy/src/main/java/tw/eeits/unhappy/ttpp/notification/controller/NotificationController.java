package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberService;
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
@RequestMapping("/app/notifications")
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
    public ResponseEntity<ApiRes<List<NotificationTemplate>>> findAllTemplates(@RequestBody NotificationQuery query) {
        List<NotificationTemplate> foundEntry = notificationService.findTemplatesByCriteria(query);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================






    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @PostMapping("/user/query")
    public ResponseEntity<ApiRes<List<NotificationPublished>>> findUserNotifications(@RequestBody NotificationQuery query) {
        List<NotificationPublished> foundEntry = notificationService.findNotificationsByCriteria(query);
        return ResponseEntity.ok(ResponseFactory.success(foundEntry));
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================






}
