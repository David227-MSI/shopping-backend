package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberService;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
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
    public ResponseEntity<ApiRes<NotificationTemplate>> createTemplate(
        @RequestBody NotificationTemplateRequest request
    ) {

        // verify data type
        Set<ConstraintViolation<NotificationTemplateRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail(errorMessages));
        }




        // transfer data from DTO to Entity
        NotificationTemplate newEntry = NotificationTemplate.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeType(request.getNoticeType())
                .build();
        NotificationTemplate savedEntry = notificationService.createTemplate(newEntry);

        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }
    
    @PostMapping("/publish")
    public ResponseEntity<ApiRes<NotificationPublished>> publishNotification(
        @RequestBody NotificationPublishRequest request
    ) {
        
        // verify data
        NotificationTemplate foundTemplate = notificationService.findTemplateById(request.getTemplateId());
        if(foundTemplate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail("找不到套用的訊息模板"));
        }

        // check user
        UserMember foundUser = userMemberService.findUserById(request.getUserId());
        if(foundUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail("找不到目標用戶"));
        }

        // transfer data from DTO to Entity
        NotificationPublished notification = NotificationPublished.builder()
            .userMember(foundUser)
            .notificationTemplate(foundTemplate)
            .isRead(false)
            .expiredAt(request.getExpiredAt())
            .build();
        NotificationPublished savedEntry = notificationService.publishNotification(notification);
        // publish notification
        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
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
