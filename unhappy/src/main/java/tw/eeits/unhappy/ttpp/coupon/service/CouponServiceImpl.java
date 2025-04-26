package tw.eeits.unhappy.ttpp.coupon.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
    private final CouponTemplateRepository templateRepository;
    private final CouponPublishedRepository publishedRepository;
    private final Validator validator;

    // =================================================================
    // 建立優惠相關======================================================
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
    // 建立優惠相關======================================================
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
    public List<CouponTemplate> findTemplatesByCriteria(CouponQuery query) {
        return templateRepository.findAll(CouponTemplateRepository.byTemplatesCriteria(query));
    }
    // =================================================================
    // 基本查詢相關======================================================
    // =================================================================


    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================
    @Override
    public List<CouponPublished> findCouponsByCriteria(CouponQuery query) {
        return publishedRepository.findAll(CouponPublishedRepository.byCouponsCriteria(query));
    }
    // =================================================================
    // 用戶操作相關======================================================
    // =================================================================

}
