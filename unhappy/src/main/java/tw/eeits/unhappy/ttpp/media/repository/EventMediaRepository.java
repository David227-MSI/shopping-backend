package tw.eeits.unhappy.ttpp.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.ttpp.media.model.EventMedia;

@Repository
public interface EventMediaRepository extends JpaRepository<EventMedia, Integer>{

}
