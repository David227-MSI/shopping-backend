package tw.eeits.unhappy.ttpp.event.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import tw.eeits.unhappy.ttpp.event.dto.EventAdminQuery;
import tw.eeits.unhappy.ttpp.event.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event>{

    static Specification<Event> byEventCriteria(EventAdminQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // EventName
            if (query.getEventName() != null && !query.getEventName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("eventName")), "%" + query.getEventName().toLowerCase().trim() + "%"));
            }

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

            // AnnounceTime
            if (query.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("endTime"), query.getEndTime()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }

}
