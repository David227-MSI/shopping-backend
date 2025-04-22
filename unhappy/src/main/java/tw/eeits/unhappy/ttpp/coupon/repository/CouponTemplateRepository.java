package tw.eeits.unhappy.ttpp.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Integer>{

}
