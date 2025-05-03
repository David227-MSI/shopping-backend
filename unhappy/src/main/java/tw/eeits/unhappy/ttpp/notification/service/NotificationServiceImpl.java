package tw.eeits.unhappy.ttpp.notification.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.ttpp._itf.NotificationService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
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



    // =================================================================
    // 建立相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<NotificationTemplate> createTemplate(NotificationTemplate template) {
        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if (template == null) {
            ec.add("輸入資料為 null");
        } else {
            ec.validate(template, validator);
        }

        // service logic




        
        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            NotificationTemplate savedEntry = templateRepository.save(template);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            System.out.println("建立訊息模板錯誤: " + e);
            return ServiceResponse.fail("建立失敗: " + e.getMessage());
        }
    }
        
    @Override
    public ServiceResponse<NotificationPublished> publishNotification(NotificationPublished notification) {
        Set<String> errors = new HashSet<>();

        // check input and verify datatype
        if (notification == null) {
            errors.add("輸入資料為 null");
        } else {
            if (notification.getUserMember() == null) {
                errors.add("輸入用戶為 null");
            }
            if (notification.getNotificationTemplate() == null) {
                errors.add("輸入訊息模板為 null");
            }
            // validator
            errors.addAll(
                validator.validate(notification)
                    .stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.toSet())
            );
        }
        // service logic





        if (!errors.isEmpty()) {
            return ServiceResponse.fail(String.join("; ", errors));
        }

        // service operation
        try {
            NotificationPublished savedEntry = publishedRepository.save(notification);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("通知訊息推送失敗: " + e.getMessage());
        }
    }
    // =================================================================
    // 建立相關==========================================================
    // =================================================================




    // =================================================================
    // 修改相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<NotificationPublished> markNotificationAsRead(Integer id) {
        
        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if(id == null) {ec.add("請輸入通知訊息ID");}

        // service logic
        NotificationPublished foundNotification = publishedRepository.findById(id).orElse(null);        

        if(foundNotification == null) {
            ec.add("找不到該通知訊息");
        } else {
            if(foundNotification.getIsRead()) {ec.add("通知訊息已讀過");}  
        } 

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {

            foundNotification.setIsRead(true);

            NotificationPublished savedEntry = publishedRepository.save(foundNotification);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("修改通知訊息發生錯誤: " + e.getMessage());
        }
    }
    // =================================================================
    // 修改相關==========================================================
    // =================================================================












    // =================================================================
    // 刪除相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<Boolean> deleteNotificationById(Integer id) {
        try {
            publishedRepository.deleteById(id);
            return ServiceResponse.success(true);
        } catch (Exception e) {
            return ServiceResponse.fail(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> deleteNotificationByUserMember(UserMember userMember) {
        try {
            publishedRepository.deleteByUserMember(userMember);
            return ServiceResponse.success(true);
        } catch (Exception e) {
            return ServiceResponse.fail(e.getMessage());
        }
    }
    // =================================================================
    // 刪除相關==========================================================
    // =================================================================













    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    @Override
    public NotificationTemplate findTemplateById(Integer id) {
        return templateRepository.findById(id).orElse(null);
    }

    @Override
    public ServiceResponse<NotificationPublished> findNotificationById(Integer id) {

        ErrorCollector ec = new ErrorCollector();

        if(id == null) {ec.add("請輸入用戶通知訊息ID");}

        // service logic
        NotificationPublished foundEntry = publishedRepository.findById(id).orElse(null);
        if(foundEntry == null) {ec.add("找不到該通知訊息");}

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            if(foundEntry.getIsRead()) {
                return ServiceResponse.success(foundEntry);
            }
            foundEntry.setIsRead(true);
            NotificationPublished savedEntry = publishedRepository.save(foundEntry);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢用戶通知訊息異常: " + e.getMessage());
        }
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================






    // =================================================================
    // 條件查詢相關======================================================
    // =================================================================
    @Override
    public ServiceResponse<List<NotificationTemplate>> findTemplatesByCriteria(NotificationQuery query) {

        // service operation
        try {
            List<NotificationTemplate> res = templateRepository.findAll(NotificationTemplateRepository.byTemplatesCriteria(query));
            return ServiceResponse.success(res);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢發生異常: " + e.getMessage());
        }
    }
    // =================================================================
    // 條件查詢相關======================================================
    // =================================================================





    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @Override
    public ServiceResponse<List<NotificationPublished>> findNotificationsByCriteria(NotificationQuery query) {
        
        // service operation
        try {
            List<NotificationPublished> res = publishedRepository.findAll(NotificationPublishedRepository.byNotificationsCriteria(query));
            return ServiceResponse.success(res);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢發生異常: " + e.getMessage());
        }
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================



    // @Override
    // public List<NotificationPublished> findNotificationsByUser(Integer userId) {
    //     if (userId == null) {
    //         return List.of(); // 返回空列表，而不是拋異常
    //     }
    //     return publishedRepository.findByUserId(userId);
    // }








}
