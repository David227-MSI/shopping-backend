package tw.eeits.unhappy.ttpp.notificationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;
import tw.eeits.unhappy.ttpp.notification.repository.SubscribeListRepository;

@SpringBootTest
public class SubscribeListTests {
    @Autowired
    private SubscribeListRepository subscribeListRepository;

    @Autowired
    private UserMemberRepository userMemberRepository;

    @Test
    public void testSaveAndFindById() {

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        SubscribeList newEntry = SubscribeList.builder()
            .userMember(foundUser)
            .itemId(3)
            .itemType(ItemType.BRAND)
            .isSubscribing(false)
            .build();
        SubscribeList savedEntry = subscribeListRepository.save(newEntry);
        SubscribeList foundEntry = subscribeListRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(savedEntry);
        assertEquals(savedEntry.getUserMember(), foundEntry.getUserMember());
        assertEquals(newEntry.getItemId(), foundEntry.getItemId());
        assertEquals(newEntry.getItemType(), foundEntry.getItemType());
        assertEquals(newEntry.getIsSubscribing(), foundEntry.getIsSubscribing());
    }

    @Test
    public void testUpdateById() {

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        SubscribeList newEntry = SubscribeList.builder()
            .userMember(foundUser)
            .itemId(3)
            .itemType(ItemType.BRAND)
            .isSubscribing(false)
            .build();
        SubscribeList savedEntry = subscribeListRepository.save(newEntry);

        SubscribeList modEntry = SubscribeList.builder()
            .id(savedEntry.getId())
            .userMember(foundUser)
            .itemId(4)
            .itemType(ItemType.PRODUCT)
            .isSubscribing(true)
            .build();
        subscribeListRepository.save(modEntry);

        SubscribeList foundEntry = subscribeListRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(modEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getUserMember(), foundEntry.getUserMember());
        assertEquals(modEntry.getItemId(), foundEntry.getItemId());
        assertEquals(modEntry.getItemType(), foundEntry.getItemType());
        assertEquals(modEntry.getIsSubscribing(), foundEntry.getIsSubscribing());
    }

    @Test
    public void testDeleteById() {

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);


        SubscribeList newEntry = SubscribeList.builder()
            .userMember(foundUser)
            .itemId(3)
            .itemType(ItemType.BRAND)
            .isSubscribing(false)
            .build();
        SubscribeList savedEntry = subscribeListRepository.save(newEntry);

        subscribeListRepository.deleteById(savedEntry.getId());
        SubscribeList foundEntry = subscribeListRepository.findById(savedEntry.getId()).orElse(null);

        assertNull(foundEntry);
    }



}
