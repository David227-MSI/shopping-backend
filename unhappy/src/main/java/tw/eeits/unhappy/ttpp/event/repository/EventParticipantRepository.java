package tw.eeits.unhappy.ttpp.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.ttpp.event.model.EventParticipant;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Integer>{
    Integer countByUserMemberIdAndEventId(Integer userMemberId, Integer eventId);
}
