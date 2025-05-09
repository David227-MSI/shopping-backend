package tw.eeits.unhappy.ttpp.coupon.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Integer>, JpaSpecificationExecutor<CouponTemplate> {

    static Specification<CouponTemplate> byTemplatesCriteria(CouponQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // StartTime
            if (query.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startTime"), query.getStartTime()));
            }

            // EndTime
            if (query.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("endTime"), query.getEndTime()));
            }

            // Min Discount Value
            if (query.getMinDiscountValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("discountValue"), query.getMinDiscountValue()));
            }

            // Max Discount Value
            if (query.getMaxDiscountValue() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("discountValue"), query.getMaxDiscountValue()));
            }

            // Discount Type
            if (query.getDiscountType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("discountType"), query.getDiscountType()));
            }

            // Applicable Type
            if (query.getApplicableType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("applicableType"), query.getApplicableType()));
            }






            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }




    @Query("SELECT ct FROM CouponTemplate ct " +
           "WHERE (:startTime IS NULL OR ct.endTime IS NULL OR ct.endTime >= :startTime) ")
    List<CouponTemplate> findValidCouponTemplates(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );



}
