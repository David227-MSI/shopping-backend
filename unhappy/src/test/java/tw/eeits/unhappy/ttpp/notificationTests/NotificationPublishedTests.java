package tw.eeits.unhappy.ttpp.notificationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.notification.enums.NoticeType;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;
import tw.eeits.unhappy.ttpp.notification.repository.NotificationPublishedRepository;
import tw.eeits.unhappy.ttpp.notification.repository.NotificationTemplateRepository;

@SpringBootTest
public class NotificationPublishedTests {

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    private NotificationPublishedRepository notificationPublishedRepository;

    @Test
    public void testSaveAndFindById() {
        NotificationTemplate template = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);

        NotificationPublished newEntry = NotificationPublished.builder()
            .notificationTemplate(savedTemplate)
            .userId(1001) // to be arranged after user fk created
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        
        NotificationPublished savedEntry = notificationPublishedRepository.save(newEntry);

        assertNotNull(savedEntry);
        assertEquals(savedTemplate.getId(), savedEntry.getNotificationTemplate().getId());
        assertEquals(1001, savedEntry.getUserId()); // to be arranged after user fk created
        assertEquals(false, savedEntry.getIsRead());
    }

    @Test
    public void testUpdateById() {
        NotificationTemplate template = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);

        NotificationPublished newEntry = NotificationPublished.builder()
            .notificationTemplate(savedTemplate)
            .userId(1001) // to be arranged after user fk created
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        
        NotificationPublished savedEntry = notificationPublishedRepository.save(newEntry);

        NotificationPublished modEntry = NotificationPublished.builder()
            .id(savedEntry.getId())
            .notificationTemplate(savedTemplate)
            .userId(1003) // to be arranged after user fk created
            .isRead(true)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        
        notificationPublishedRepository.save(modEntry);
        NotificationPublished foundEntry = notificationPublishedRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(1003, foundEntry.getUserId());
        assertEquals(true, foundEntry.getIsRead());
    }

    @Test
    public void testDeleteById() {
        NotificationTemplate template = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);

        NotificationPublished newEntry = NotificationPublished.builder()
            .notificationTemplate(savedTemplate)
            .userId(1001) // to be arranged after user fk created
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        
        NotificationPublished savedEntry = notificationPublishedRepository.save(newEntry);
        notificationPublishedRepository.deleteById(savedEntry.getId());

        NotificationPublished foundEntry = notificationPublishedRepository.findById(savedEntry.getId()).orElse(null);
        assertNull(foundEntry);
    }

}
