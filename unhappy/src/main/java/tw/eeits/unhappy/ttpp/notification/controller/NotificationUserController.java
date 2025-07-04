package tw.eeits.unhappy.ttpp.notification.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;


@RestController
@RequestMapping("/api/user/notifications")
@RequiredArgsConstructor
public class NotificationUserController {

    private final NotificationService notificationService;
    private final UserMemberService userMemberService;



    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================

    @GetMapping("/unread/{userId}")
    public ResponseEntity<ApiRes<Integer>> getUnreadNotificationCount(
        @PathVariable Integer userId
    ) {

        // call service
        ServiceResponse<Integer> res = notificationService.getUnreadNotificationCount(userId);
        
        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        return ResponseEntity.ok(ResponseFactory.success(res.getData()));
    }


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
    // 修改相關==========================================================
    // =================================================================

    @PutMapping("/markAllAsRead/{userId}")
    public ResponseEntity<ApiRes<Map<String, Object>>> markAllNotificationsAsRead(
        @PathVariable Integer userId
    ) {

        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = null;

        if(userId == null) {
            ec.add("請輸入用戶ID");
        } else {
            foundUser = userMemberService.findUserById(userId);
            if(foundUser == null) {ec.add("找不到操作用戶");}
        }

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<Integer> res = notificationService.markAllAsReadByUserMember(foundUser);

        if (!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("updatedCount", res.getData());
        return ResponseEntity.ok(ResponseFactory.success(data));

    }



    // =================================================================
    // 修改相關==========================================================
    // =================================================================







    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiRes<Boolean>> deleteUserNotification(
        @PathVariable Integer id
    ) {
        ErrorCollector ec = new ErrorCollector();

        if(id == null) {ec.add("找不到目標訊息");}
        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<Boolean> res = notificationService.deleteNotificationById(id);
        if (!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseFactory.fail(res.getMessage()));
        }
        return ResponseEntity.ok(ResponseFactory.success(res.getData()));
    }

    @DeleteMapping("/deleteAll/{userId}")
    public ResponseEntity<ApiRes<Boolean>> deleteNotificationByUser(
        @PathVariable Integer userId
    ) {
        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = userMemberService.findUserById(userId);

        if(userId == null) {
            ec.add("請輸入用戶ID");
        } else if(foundUser == null) {
            ec.add("找不到操作用戶");
        }
        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<Boolean> res = notificationService.deleteNotificationByUserMember(foundUser);
        if (!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseFactory.fail(res.getMessage()));
        }
        return ResponseEntity.ok(ResponseFactory.success(res.getData()));
    }


    @PostMapping("/query")
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
            mp.put("isRead", notification.getIsRead());
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

    @GetMapping("/notification/{id}")
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
