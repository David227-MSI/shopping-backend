package tw.eeits.unhappy.ttpp.notificationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.notification.enums.ItemType;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;
import tw.eeits.unhappy.ttpp.notification.repository.SubscribeListRepository;

@SpringBootTest
public class SubscribeListTests {
    @Autowired
    private SubscribeListRepository subscribeListRepository;

    @Test
    public void testSaveAndFindById() {

        SubscribeList newEntry = SubscribeList.builder()
            .userId(1001)
            .itemId(3)
            .itemType(ItemType.BRAND)
            .isSubscribing(false)
            .build();
        SubscribeList savedEntry = subscribeListRepository.save(newEntry);
        SubscribeList foundEntry = subscribeListRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(savedEntry);
        assertEquals(savedEntry.getUserId(), foundEntry.getUserId());
        assertEquals(newEntry.getItemId(), foundEntry.getItemId());
        assertEquals(newEntry.getItemType(), foundEntry.getItemType());
        assertEquals(newEntry.getIsSubscribing(), foundEntry.getIsSubscribing());
    }

    @Test
    public void testUpdateById() {

        SubscribeList newEntry = SubscribeList.builder()
            .userId(1001)
            .itemId(3)
            .itemType(ItemType.BRAND)
            .isSubscribing(false)
            .build();
        SubscribeList savedEntry = subscribeListRepository.save(newEntry);

        SubscribeList modEntry = SubscribeList.builder()
            .id(savedEntry.getId())
            .userId(1002)
            .itemId(4)
            .itemType(ItemType.PRODUCT)
            .isSubscribing(true)
            .build();
        subscribeListRepository.save(modEntry);

        SubscribeList foundEntry = subscribeListRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(modEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getUserId(), foundEntry.getUserId());
        assertEquals(modEntry.getItemId(), foundEntry.getItemId());
        assertEquals(modEntry.getItemType(), foundEntry.getItemType());
        assertEquals(modEntry.getIsSubscribing(), foundEntry.getIsSubscribing());
    }

    @Test
    public void testDeleteById() {

        SubscribeList newEntry = SubscribeList.builder()
            .userId(1001)
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
