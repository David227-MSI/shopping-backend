package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationPublishRequest;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationTemplateRequest;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

@RestController
@RequestMapping("/app/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final Validator validator;

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
            
            return ResponseEntity.badRequest().body(ResponseFactory.fail(errorMessages));
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
            return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到套用的訊息模板"));
        }

        // UserMember foundUser = userMemberService.findUserById(request.getUserId());
        // if(foundUser == null) {
        //     return ResponseEntity.badRequest().body(ResponseFactory.fail("找不到目標用戶"));
        // }


        // transfer data from DTO to Entity
        NotificationPublished notification = NotificationPublished.builder()
            .userId(request.getUserId())
            .notificationTemplate(foundTemplate)
            .isRead(false)
            .expiredAt(request.getExpiredAt())
            .build();
        NotificationPublished savedEntry = notificationService.publishNotification(notification);
        // publish notification
        return ResponseEntity.ok(ResponseFactory.success(savedEntry));
    }
}
