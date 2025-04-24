package tw.eeits.unhappy.ttpp.coupon.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.CouponService;
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

    @Override
    public CouponTemplate createTemplate(CouponTemplate template) {
        
        // check input parameter
        if(template == null) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<CouponTemplate>> violations = validator.validate(template);
        if(!violations.isEmpty()) {
            System.out.println(violations);
            return null;
        }

        try {
            return templateRepository.save(template);
        } catch (Exception e) {
            System.out.println("建立優惠券模板發生異常: " + e);
            return null;
        }

    }

    @Override
    public CouponPublished publishCoupon(CouponPublished couponPublished) {
        // check input parameter
        if (couponPublished == null || 
            couponPublished.getUserId() == null || 
            couponPublished.getCouponTemplate() == null
        ) {
            return null;
        }

        // verify datatype
        Set<ConstraintViolation<CouponPublished>> violations = validator.validate(couponPublished);
        if (!violations.isEmpty()) {
            System.out.println(violations);
            return null;
        }

        try {
            return publishedRepository.save(couponPublished);
        } catch (Exception e) {
            System.out.println("發送優惠券發生錯誤: " + e);
            return null;
        }
    }

    @Override
    public CouponTemplate findTemplateById(Integer id) {
        return templateRepository.findById(id).orElse(null);
    }

}
