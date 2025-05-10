package tw.eeits.unhappy.ttpp.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.ttpp.event.model.EventPrize;

public interface EventPrizeRepository extends JpaRepository<EventPrize, Integer>{
    List<EventPrize> findByEventId(Integer eventId);
}
