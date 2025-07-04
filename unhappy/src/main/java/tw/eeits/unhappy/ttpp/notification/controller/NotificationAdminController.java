package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.HashMap;
import java.util.List;
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
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationPublishRequest;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationTemplateRequest;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class NotificationAdminController {

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
        List<NotificationTemplate> templateList = res.getData();
        

        Map<String, Object> data = new HashMap<>();
        data.put("templateList", templateList);
        
        return ResponseEntity.ok(ResponseFactory.success(data));
    }






}
