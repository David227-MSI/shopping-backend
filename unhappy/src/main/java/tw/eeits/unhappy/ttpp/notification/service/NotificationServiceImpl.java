package tw.eeits.unhappy.ttpp.notification.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;
import tw.eeits.unhappy.ttpp.notification.repository.NotificationPublishedRepository;
import tw.eeits.unhappy.ttpp.notification.repository.NotificationTemplateRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTemplateRepository templateRepository;
    private final NotificationPublishedRepository publishedRepository;
    private final Validator validator;


    @Override
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        
        
        if(template == null) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<NotificationTemplate>> violations = validator.validate(template);
        if(!violations.isEmpty()) {
            return null;
        }

        try {
            return templateRepository.save(template);
        } catch (Exception e) {
            System.out.println("建立訊息模板錯誤: " + e);
            return null;
        }
    }
        
    @Override
    public NotificationPublished publishNotification(NotificationPublished notificationPublished) {
        // check input parameter
        if (notificationPublished == null || 
            notificationPublished.getUserId() == null || 
            notificationPublished.getNotificationTemplate() == null
        ) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<NotificationPublished>> violations = validator.validate(notificationPublished);
        if (!violations.isEmpty()) {
            return null;
        }

        try {
            return publishedRepository.save(notificationPublished);
        } catch (Exception e) {
            // 資料庫異常等，統一返回 null
            System.out.println("通知訊息發送錯誤: " + e);
            return null;
        }
    }

    // @Override
    // public List<NotificationPublished> findNotificationsByUser(Integer userId) {
    //     if (userId == null) {
    //         return List.of(); // 返回空列表，而不是拋異常
    //     }
    //     return publishedRepository.findByUserId(userId);
    // }



    @Override
    public NotificationTemplate findTemplateById(Integer id) {
        return templateRepository.findById(id).orElse(null);
    }




}
