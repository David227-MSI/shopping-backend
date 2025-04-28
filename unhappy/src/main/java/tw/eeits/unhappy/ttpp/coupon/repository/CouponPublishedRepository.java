package tw.eeits.unhappy.ttpp.coupon.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;

@Repository
public interface CouponPublishedRepository extends JpaRepository<CouponPublished, String>, JpaSpecificationExecutor<CouponPublished> {

    Optional<CouponPublished> findCouponById(String id);

    static Specification<CouponPublished> byCouponsCriteria(CouponQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // join tables
            Join<CouponPublished, UserMember> userMember = root.join("userMember");
            Join<CouponPublished, CouponTemplate> template = root.join("couponTemplate");

            // UserMember (required)
            predicates.add(criteriaBuilder.equal(userMember.get("id"), query.getUserId()));

            // IsUsed
            if (query.getIsUsed() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isUsed"), query.getIsUsed()));
            }

            // StartTime
            if (query.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        template.get("startTime"), query.getStartTime()));
            }

            // EndTime
            if (query.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        template.get("endTime"), query.getEndTime()));
            }

            // Min Discount Value
            if (query.getMinDiscountValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        template.get("discountValue"), query.getMinDiscountValue()));
            }

            // Max Discount Value
            if (query.getMaxDiscountValue() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        template.get("discountValue"), query.getMaxDiscountValue()));
            }

            // Discount Type
            if (query.getDiscountType() != null) {
                predicates.add(criteriaBuilder.equal(
                        template.get("discountType"), query.getDiscountType()));
            }

            // Applicable Type
            if (query.getApplicableType() != null) {
                predicates.add(criteriaBuilder.equal(
                        template.get("applicableType"), query.getApplicableType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }






}
