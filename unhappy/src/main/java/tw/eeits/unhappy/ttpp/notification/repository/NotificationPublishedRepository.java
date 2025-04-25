package tw.eeits.unhappy.ttpp.notification.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;

@Repository
public interface NotificationPublishedRepository extends JpaRepository<NotificationPublished, Integer>, JpaSpecificationExecutor<NotificationPublished> {

    static Specification<NotificationPublished> byNotificationsCriteria(NotificationQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // UserId (required)
            predicates.add(criteriaBuilder.equal(root.get("userMember").get("id"), query.getUserId()));

            // IsRead
            if (query.getIsRead() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isRead"), query.getIsRead()));
            }

            // CreatedAt
            if (query.getCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"), query.getCreatedAt()));
            }

            // Title
            if (query.getTitle() != null && !query.getTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("notificationTemplate").get("title")), 
                        "%" + query.getTitle().toLowerCase().trim() + "%"));
            }

            // NoticeType
            if(query.getNoticeType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("notificationTemplate").get("noticeType"), 
                query.getNoticeType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }



}
