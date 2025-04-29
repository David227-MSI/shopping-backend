package tw.eeits.unhappy.ttpp.notification.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.ttpp.notification.dto.NotificationQuery;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Integer>, JpaSpecificationExecutor<NotificationTemplate> {

    static Specification<NotificationTemplate> byTemplatesCriteria(NotificationQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Title
            if (query.getTitle() != null && !query.getTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), "%" + query.getTitle().toLowerCase().trim() + "%"));
            }

            // NoticeType
            if(query.getNoticeType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("noticeType"), query.getNoticeType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
