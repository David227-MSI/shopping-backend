package tw.eeits.unhappy.ttpp.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.event.enums.EventStatus;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;

@SpringBootTest
public class EventRepositoryTests {
    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testSaveAndFindById() {
        Event newEntry = Event.builder()
            .eventName("Spring Sale")
            .minSpend(new BigDecimal("100.00"))
            .maxEntries(10)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(7))
            .announceTime(LocalDateTime.now().plusDays(8))
            .eventStatus(EventStatus.ANNOUNCED)
            .establishedBy("Admin")
            .build();
        Event savedEntry = eventRepository.save(newEntry);

        assertNotNull(savedEntry);
        assertEquals(newEntry.getEventName(), savedEntry.getEventName());
        assertEquals(newEntry.getMinSpend(), savedEntry.getMinSpend());
        assertEquals(newEntry.getMaxEntries(), savedEntry.getMaxEntries());
        assertEquals(newEntry.getStartTime(), savedEntry.getStartTime());
        assertEquals(newEntry.getEndTime(), savedEntry.getEndTime());
        assertEquals(newEntry.getAnnounceTime(), savedEntry.getAnnounceTime());
        assertEquals(newEntry.getEventStatus(), savedEntry.getEventStatus());
        assertEquals(newEntry.getEstablishedBy(), savedEntry.getEstablishedBy());
        assertNotNull(savedEntry.getCreatedAt());
    }

}
