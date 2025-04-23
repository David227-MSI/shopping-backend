package tw.eeits.unhappy.ttpp.media;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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





            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(7))
            .build();
        Event savedEvent = eventRepository.save(event);

        EventMedia newEntry = EventMedia.builder()
            .event(savedEvent)
            .mediaData("test_video".getBytes())
            .mediaType(MediaType.VIDEO)
            .build();
    }
}
