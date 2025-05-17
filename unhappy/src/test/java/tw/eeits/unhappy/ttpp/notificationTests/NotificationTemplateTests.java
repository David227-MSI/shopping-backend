package tw.eeits.unhappy.ttpp.notificationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.notification.enums.NoticeType;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;
import tw.eeits.unhappy.ttpp.notification.repository.NotificationTemplateRepository;

@SpringBootTest
public class NotificationTemplateTests {
    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;


    @Test
    public void testSaveAndFindById() {

        NotificationTemplate newEntry = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedEntry = notificationTemplateRepository.save(newEntry);

        assertNotNull(savedEntry);
        assertEquals(newEntry.getTitle(), savedEntry.getTitle());
        assertEquals(newEntry.getContent(), savedEntry.getContent());
        assertEquals(newEntry.getNoticeType(), savedEntry.getNoticeType());
    }

    @Test
    public void testUpdateById() {

        NotificationTemplate newEntry = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedEntry = notificationTemplateRepository.save(newEntry);

        NotificationTemplate modEntry = NotificationTemplate.builder()
            .id(savedEntry.getId())
            .title("Order Inform")
            .content("Order created!")
            .noticeType(NoticeType.ORDER)
            .build();
        notificationTemplateRepository.save(modEntry);

        NotificationTemplate foundEntry = notificationTemplateRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getTitle(), foundEntry.getTitle());
        assertEquals(modEntry.getContent(), foundEntry.getContent());
        assertEquals(modEntry.getNoticeType(), foundEntry.getNoticeType());
    }

    @Test
    public void testDeleteById() {
        
        NotificationTemplate newEntry = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedEntry = notificationTemplateRepository.save(newEntry);
        notificationTemplateRepository.deleteById(savedEntry.getId());

        NotificationTemplate foundEntry = notificationTemplateRepository.findById(savedEntry.getId()).orElse(null);
        assertNull(foundEntry);
    }

}
