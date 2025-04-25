package tw.eeits.unhappy.ttpp.coupon.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;

@Repository
public interface CouponPublishedRepository extends JpaRepository<CouponPublished, String>, JpaSpecificationExecutor<CouponPublished> {

    static Specification<CouponPublished> byCouponsCriteria(CouponQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // UserID (required)
            predicates.add(criteriaBuilder.equal(root.get("userId"), query.getUserId()));

            // IsUsed
            if (query.getIsUsed() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isUsed"), query.getIsUsed()));
            }

            // StartTime
            if (query.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("couponTemplate").get("startTime"), query.getStartTime()));
            }

            // EndTime
            if (query.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("couponTemplate").get("endTime"), query.getEndTime()));
            }

            // Min Discount Value
            if (query.getMinDiscountValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("couponTemplate").get("discountValue"), query.getMinDiscountValue()));
            }

            // Max Discount Value
            if (query.getMaxDiscountValue() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("couponTemplate").get("discountValue"), query.getMaxDiscountValue()));
            }

            // Discount Type
            if (query.getDiscountType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("couponTemplate").get("discountType"), query.getDiscountType()));
            }

            // Applicable Type
            if (query.getApplicableType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("couponTemplate").get("applicableType"), query.getApplicableType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }






}
