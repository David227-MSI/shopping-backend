package tw.eeits.unhappy.ttpp.notification.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

@Repository
public interface NotificationPublishedRepository extends JpaRepository<NotificationPublished, Integer>, JpaSpecificationExecutor<NotificationPublished> {


    void deleteByUserMember(UserMember userMember);

    List<NotificationPublished> findByUserMemberAndIsRead(UserMember userMember, Boolean isRead);

    @Modifying
    @Query("UPDATE NotificationPublished n SET n.isRead = true WHERE n.userMember = :userMember AND n.isRead = false")
    Integer markAllAsReadByUserMember(@Param("userMember") UserMember userMember);




    static Specification<NotificationPublished> byNotificationsCriteria(NotificationQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<NotificationPublished, UserMember> userJoin =  root.join("userMember");
            Join<NotificationPublished, NotificationTemplate> templateJoin = root.join("notificationTemplate");

            // UserMember (required)
            predicates.add(criteriaBuilder.equal(userJoin.get("id"), query.getUserId()));

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
                        criteriaBuilder.lower(templateJoin.get("title")), 
                        "%" + query.getTitle().toLowerCase().trim() + "%"));
            }

            // NoticeType
            if(query.getNoticeType() != null) {
                predicates.add(criteriaBuilder.equal(templateJoin.get("noticeType"), 
                query.getNoticeType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
