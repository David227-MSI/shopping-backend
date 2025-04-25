package tw.eeits.unhappy.ttpp.notificationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberRepository;
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

    @Autowired
    private UserMemberRepository userMemberRepository;

    @Test
    public void testSaveAndFindById() {
        NotificationTemplate template = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        NotificationPublished newEntry = NotificationPublished.builder()
            .notificationTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        NotificationPublished savedEntry = notificationPublishedRepository.save(newEntry);

        assertNotNull(savedEntry);
        assertEquals(newEntry.getNotificationTemplate().getId(), savedEntry.getNotificationTemplate().getId());
        assertEquals(newEntry.getUserMember(), savedEntry.getUserMember()); // to be arranged after user fk created
        assertEquals(newEntry.getIsRead(), savedEntry.getIsRead());
    }

    @Test
    public void testUpdateById() {
        NotificationTemplate template = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        NotificationPublished newEntry = NotificationPublished.builder()
            .notificationTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        NotificationPublished savedEntry = notificationPublishedRepository.save(newEntry);

        NotificationPublished modEntry = NotificationPublished.builder()
            .id(savedEntry.getId())
            .notificationTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .isRead(true)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        notificationPublishedRepository.save(modEntry);
        NotificationPublished foundEntry = notificationPublishedRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(modEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getUserMember(), foundEntry.getUserMember());
        assertEquals(modEntry.getIsRead(), foundEntry.getIsRead());
    }

    @Test
    public void testDeleteById() {
        NotificationTemplate template = NotificationTemplate.builder()
            .title("Promotion Alert")
            .content("New promotion available!")
            .noticeType(NoticeType.PROMOTION)
            .build();
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        NotificationPublished newEntry = NotificationPublished.builder()
            .notificationTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
        
        NotificationPublished savedEntry = notificationPublishedRepository.save(newEntry);
        notificationPublishedRepository.deleteById(savedEntry.getId());

        NotificationPublished foundEntry = notificationPublishedRepository.findById(savedEntry.getId()).orElse(null);
        assertNull(foundEntry);
    }

}
