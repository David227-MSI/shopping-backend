package tw.eeits.unhappy.ttpp.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp.notification.dto.PublishRequest;
import tw.eeits.unhappy.ttpp.notification.dto.TemplateRequest;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

@RestController
@RequestMapping("/app/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/template")
    public ResponseEntity<NotificationTemplate> createTemplate(
        @RequestBody TemplateRequest request
    ) {

        // transfer data from DTO to Entity
        NotificationTemplate template = NotificationTemplate.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeType(request.getNoticeType())
                .build();
        System.out.println(template);

        // create template
        return ResponseEntity.ok(notificationService.createTemplate(template));
    }
    
    @PostMapping("/publish")
    public ResponseEntity<NotificationPublished> publishNotification(
        @Valid @RequestBody PublishRequest request
    ) {
        System.out.println(request);
        // transfer data from DTO to Entity
        NotificationTemplate foundTemplate = notificationService.findTemplateById(request.getTemplateId());
        NotificationPublished notification = NotificationPublished.builder()
            .userId(request.getUserId())
            .notificationTemplate(foundTemplate)
            .isRead(false)
            .expiredAt(request.getExpiredAt())
            .build();

        // publish notification
        return ResponseEntity.ok(notificationService.publishNotification(notification));
    }





}
