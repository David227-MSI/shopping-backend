package tw.eeits.unhappy.ttpp.coupon.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponPublishedRepository;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponTemplateRepository;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.CouponMedia;
import tw.eeits.unhappy.ttpp.media.repository.CouponMediaRepository;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final UserMemberRepository userMemberRepository;
    private final CouponTemplateRepository templateRepository;
    private final CouponPublishedRepository publishedRepository;
    private final CouponMediaRepository mediaRepository;
    private final EventRepository eventRepository;
    private final Validator validator;

    // =================================================================
    // 建立相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<CouponTemplate> createTemplate(CouponTemplate template) {
        
        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if(template == null) {
            ec.add("輸入資料為 null");
        } else {
            ec.validate(template, validator);
        }


        // service logic


        
        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            CouponTemplate savedEntry = templateRepository.save(template);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("建立優惠券模板發生異常: " + e.getMessage());
        }
    }

    @Override
    public ServiceResponse<CouponPublished> publishCoupon(CouponPublished coupon) {

        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if (coupon == null) {
            ec.add("輸入資料為 null");
        } else {
            if(coupon.getUserMember() == null) {
                ec.add("輸入用戶為 null");
            } 
            if(coupon.getCouponTemplate() == null) {
                ec.add("輸入優惠券模板為 null");
            }
            ec.validate(coupon, validator);
        }

        // service logic



        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }


        // service operation
        try {
            CouponPublished savedEntry = publishedRepository.save(coupon);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("發送優惠券發生錯誤: " + e.getMessage());
        }
    }


    @Override
    public ServiceResponse<CouponMedia> addMediaToTemplate(
        CouponTemplate template, 
        MultipartFile mediaData, 
        MediaType mediaType
    ) throws IOException {
        
        ErrorCollector ec = new ErrorCollector();

        if(template == null) {ec.add("找不到優惠券模板");}
        
        CouponMedia newEntry = CouponMedia.builder()
                .couponTemplate(template)
                .mediaType(mediaType)
                .mediaData(mediaData.getBytes())
                .build();
        try {
            CouponMedia savedEntry = mediaRepository.save(newEntry);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("圖片添加異常: " + e.getMessage());
        }
    }

    // =================================================================
    // 建立相關==========================================================
    // =================================================================




    // =================================================================
    // 修改相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<CouponPublished> couponTransfer(String couponId, String recipientMail) {

        ErrorCollector ec = new ErrorCollector();

        CouponPublished foundCoupon = null;
        UserMember foundRecipient = null;
        UserMember foundOwner = null;

        // service logic
        // check coupon is valid 
        if(couponId == null) {
            ec.add("請輸入優惠券ID");
        } else {
            foundCoupon = publishedRepository.findCouponById(couponId).orElse(null);
            if(foundCoupon == null) {
                ec.add("找不到該優惠券");
            } else if(foundCoupon.getIsUsed()) {
                ec.add("優惠券已被使用過");
            }
        }
        foundOwner = userMemberRepository.findById(foundCoupon.getUserMember().getId()).orElse(null);
    
        // check recipient
        if(recipientMail == null || recipientMail.trim() == "") {
            ec.add("受贈者信箱為必要欄位");
        } else {
            foundRecipient = userMemberRepository.findByEmail(recipientMail).orElse(null);
            if(foundRecipient == null) {ec.add("找不到受贈用戶");}
        }
        if(foundOwner.getEmail().equals(recipientMail)) {
            ec.add("優惠券無法轉贈給自己");
        } 

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            foundCoupon.setUserMember(foundRecipient);
            CouponPublished savedEntry = publishedRepository.save(foundCoupon);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("優惠券轉移發生異常: " + e.getMessage());
        }
    }



    public ServiceResponse<CouponPublished> markCouponAsUsed(String id) {

        ErrorCollector ec = new ErrorCollector();

        // check input and verify datatype
        if(id == null) {ec.add("請輸入優惠券ID");}

        // service logic
        CouponPublished foundCoupon = publishedRepository.findById(id).orElse(null);        

        if(foundCoupon == null) {
            ec.add("找不到該優惠券");
        } else {
            if(foundCoupon.getIsUsed()) {ec.add("優惠券已使用過");}  
        } 

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {

            foundCoupon.setIsUsed(true);

            CouponPublished savedEntry = publishedRepository.save(foundCoupon);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("修改優惠券發生錯誤: " + e.getMessage());
        }
    }


    @Override
    public ServiceResponse<List<CouponPublished>> getValidCouponByUserMember(UserMember userMember, BigDecimal totalAmount, List<Product> orderItems) {
        ErrorCollector ec = new ErrorCollector();

        if (userMember == null) {
            ec.add("找不到目標用戶");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            ec.add("訂單金額無效");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            ec.add("商品清單不可為空");
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        try {
            List<CouponPublished> foundList = publishedRepository.findByUserMemberAndIsUsed(userMember, false);


            foundList.forEach(c -> {
                System.out.println("Coupon ID: " + c.getId() +
                    "\nStartTime: " + c.getCouponTemplate().getStartTime() + 
                    "\nEndTime: " + c.getCouponTemplate().getEndTime()
                );
            });


            LocalDateTime currentTime = LocalDateTime.now();
            List<CouponPublished> validCoupons = foundList.stream()
                .filter(coupon -> {
                    CouponTemplate template = coupon.getCouponTemplate();
                    LocalDateTime startTime = template.getStartTime();
                    LocalDateTime endTime = template.getEndTime();

                    // check valid date
                    boolean isValidTime = (startTime == null || !currentTime.isBefore(startTime)) && 
                                        (endTime == null || !currentTime.isAfter(endTime));

                    // check minSpend
                    boolean isValidMinSpend = template.getMinSpend().compareTo(totalAmount) <= 0;

                    // check applicable type
                    boolean isValidApplicable = true;
                    ApplicableType applicableType = template.getApplicableType();
                    Integer applicableId = template.getApplicableId();
                    if (applicableType == ApplicableType.PRODUCT) {
                        isValidApplicable = orderItems.stream()
                            .anyMatch(product -> product.getId().equals(applicableId));
                    } else if (applicableType == ApplicableType.BRAND) {
                        isValidApplicable = orderItems.stream()
                            .anyMatch(product -> product.getBrand().getId().equals(applicableId));
                    }

                    // 確保所有條件都符合
                    return isValidTime && isValidMinSpend && isValidApplicable;
                })
                .collect(Collectors.toList());

                validCoupons.forEach(c -> {
                    System.out.println("Coupon ID: " + c.getId() +
                        ", Template ID: " + c.getCouponTemplate().getId());
                });


            return ServiceResponse.success(validCoupons);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢優惠券清單發生異常: " + e);
        }
    }

    // =================================================================
    // 修改相關==========================================================
    // =================================================================











    // =================================================================
    // 刪除相關==========================================================
    // =================================================================
    @Override
    public ServiceResponse<Boolean> deleteTemplateById(Integer id) {
        try {
            templateRepository.deleteById(id);
            return ServiceResponse.success(true);    
        } catch (Exception e) {
            return ServiceResponse.fail("刪除優惠券模板發生異常: " + e);
        }
    }
    // =================================================================
    // 刪除相關==========================================================
    // =================================================================












    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
    @Override
    public List<CouponTemplate> findValidCouponTemplatesForEvent(Integer eventId) {

        Event foundEvent = eventRepository.findById(eventId).orElse(null);

        return templateRepository.findValidCouponTemplates(
            foundEvent.getStartTime(),
            foundEvent.getEndTime()
        );
    }
    
    
    
    @Override
    public CouponTemplate findTemplateById(Integer id) {
        return templateRepository.findById(id).orElse(null);
    }

    @Override
    public List<CouponTemplate> findAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public List<CouponPublished> findCouponsByUserId() {
        // TO BE IMPLEMENTED
        return null;
    }

    @Override
    public ServiceResponse<List<CouponTemplate>> findTemplatesByCriteria(CouponQuery query) {

        // service operation
        try {
            List<CouponTemplate> res = templateRepository.findAll(CouponTemplateRepository.byTemplatesCriteria(query));
            return ServiceResponse.success(res);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢發生異常: " + e.getMessage());
        }
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================


    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @Override
    public ServiceResponse<List<CouponPublished>> findCouponsByCriteria(CouponQuery query) {
        
        // service operation
        try {
            List<CouponPublished> res = publishedRepository.findAll(CouponPublishedRepository.byCouponsCriteria(query));
            return ServiceResponse.success(res);
        } catch (Exception e) {
            return ServiceResponse.fail("查詢發生異常: " + e.getMessage());
        }
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================

}
