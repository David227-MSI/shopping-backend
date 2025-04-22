package tw.eeits.unhappy.ttpp.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.ttpp.event.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>{

}
