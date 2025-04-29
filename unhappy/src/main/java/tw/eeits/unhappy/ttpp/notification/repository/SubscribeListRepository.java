package tw.eeits.unhappy.ttpp.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;


@Repository
public interface SubscribeListRepository extends JpaRepository<SubscribeList, Integer>{
    SubscribeList findByUserMemberAndItemTypeAndItemId(UserMember userMember, ItemType itemType, Integer itemId);
}
