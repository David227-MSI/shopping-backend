package tw.eeits.unhappy.ttpp.eventTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    public void testUpdateById() {

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

        Event modEntry = Event.builder()
            .id(savedEntry.getId())
            .eventName("Summer Sale")
            .minSpend(new BigDecimal("220.00"))
            .maxEntries(50)
            .startTime(LocalDateTime.parse("2025-05-15T10:00:00"))
            .endTime(LocalDateTime.parse("2025-05-15T10:00:00").plusDays(5))
            .announceTime(LocalDateTime.parse("2025-05-15T10:00:00").plusDays(1))
            .eventStatus(EventStatus.ANNOUNCED)
            .establishedBy("Bob")
            .build();
            eventRepository.save(modEntry);

        Event foundEntry = eventRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(modEntry.getEventName(), foundEntry.getEventName());
        assertEquals(modEntry.getMinSpend(), foundEntry.getMinSpend());
        assertEquals(modEntry.getMaxEntries(), foundEntry.getMaxEntries());
        assertEquals(modEntry.getStartTime(), foundEntry.getStartTime());
        assertEquals(modEntry.getEndTime(), foundEntry.getEndTime());
        assertEquals(modEntry.getAnnounceTime(), foundEntry.getAnnounceTime());
        assertEquals(modEntry.getEventStatus(), foundEntry.getEventStatus());
        assertEquals(modEntry.getEstablishedBy(), foundEntry.getEstablishedBy());
        assertNotNull(foundEntry.getUpdatedAt());
    }

    @Test
    public void testDeleteById() {

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

        eventRepository.deleteById(savedEntry.getId());

        Event foundEntry = eventRepository.findById(savedEntry.getId()).orElse(null);
        
        assertNull(foundEntry);
    }

}
