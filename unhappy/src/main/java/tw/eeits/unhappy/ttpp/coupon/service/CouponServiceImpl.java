package tw.eeits.unhappy.ttpp.coupon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberRepository;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.CouponService;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponPublishedRepository;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponTemplateRepository;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final UserMemberRepository userMemberRepository;
    private final CouponTemplateRepository templateRepository;
    private final CouponPublishedRepository publishedRepository;
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
        // check recipient
        if(recipientMail == null || recipientMail.trim() == "") {
            ec.add("受贈者信箱為必要欄位");
        } else {
            foundRecipient = userMemberRepository.findByEmail(recipientMail).orElse(null);
            if(foundRecipient == null) {ec.add("找不到受贈用戶");}
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
    // =================================================================
    // 修改相關==========================================================
    // =================================================================












    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================
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
