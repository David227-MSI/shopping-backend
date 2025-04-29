package tw.eeits.unhappy.ttpp.mediaTests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;
import tw.eeits.unhappy.ttpp.media.repository.EventMediaRepository;

@SpringBootTest
public class EventMediaRepositoryTests {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMediaRepository eventMediaRepository;

    @Test
    public void testSaveAndFindById() {

        Event event = Event.builder()
            .eventName("Spring Sale")
            .minSpend(new BigDecimal("100.00"))
            .maxEntries(10)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(7))
            .announceTime(LocalDateTime.now().plusDays(8))
            .eventStatus(EventStatus.ANNOUNCED)
            .establishedBy("Admin")
            .build();
        Event savedEvent = eventRepository.save(event);

        EventMedia newEntry = EventMedia.builder()
            .event(savedEvent)
            .mediaData("test_video".getBytes())
            .mediaType(MediaType.VIDEO)
            .build();
        EventMedia savedEntry = eventMediaRepository.save(newEntry);

        EventMedia foundEntry = eventMediaRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(newEntry.getEvent().getId(), foundEntry.getEvent().getId());
        assertArrayEquals(newEntry.getMediaData(), foundEntry.getMediaData());
        assertEquals(newEntry.getMediaType(), foundEntry.getMediaType());
        assertNotNull(foundEntry.getCreatedAt());
    }

    @Test
    public void testUpdateById() {

        Event event = Event.builder()
            .eventName("Spring Sale")
            .minSpend(new BigDecimal("100.00"))
            .maxEntries(10)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(7))
            .announceTime(LocalDateTime.now().plusDays(8))
            .eventStatus(EventStatus.ANNOUNCED)
            .establishedBy("Admin")
            .build();
        Event savedEvent = eventRepository.save(event);

        EventMedia newEntry = EventMedia.builder()
            .event(savedEvent)
            .mediaData("test_video".getBytes())
            .mediaType(MediaType.VIDEO)
            .build();
        EventMedia savedEntry = eventMediaRepository.save(newEntry);

        EventMedia modEntry = EventMedia.builder()
            .id(savedEntry.getId())
            .event(savedEvent)
            .mediaData("test_image".getBytes())
            .mediaType(MediaType.IMAGE)
            .build();
        eventMediaRepository.save(modEntry);

        EventMedia foundEntry = eventMediaRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getEvent().getId(), foundEntry.getEvent().getId());
        assertArrayEquals(modEntry.getMediaData(), foundEntry.getMediaData());
        assertEquals(modEntry.getMediaType(), foundEntry.getMediaType());
        assertNotNull(foundEntry.getCreatedAt());
        assertNotNull(foundEntry.getUpdatedAt());
    }
}
