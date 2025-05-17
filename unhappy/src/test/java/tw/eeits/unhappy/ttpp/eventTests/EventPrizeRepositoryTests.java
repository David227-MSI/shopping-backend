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
import tw.eeits.unhappy.ttpp.event.enums.PrizeType;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.event.repository.EventPrizeRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;

@SpringBootTest
public class EventPrizeRepositoryTests {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventPrizeRepository eventPrizeRepository;

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

        EventPrize newEntry = EventPrize.builder()
                .event(savedEvent)
                .itemId(1)
                .itemType(PrizeType.COUPON_TEMPLATE)
                .quantity(10)
                .winRate(new BigDecimal("0.2000"))
                .totalSlots(100)
                .remainingSlots(100)
                .title("Discount Coupon")
                .build();
        EventPrize savedEntry = eventPrizeRepository.save(newEntry);
        EventPrize foundEntry = eventPrizeRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(newEntry.getEvent().getId(), foundEntry.getEvent().getId());
        assertEquals(newEntry.getItemId(), foundEntry.getItemId());
        assertEquals(newEntry.getItemType(), foundEntry.getItemType());
        assertEquals(newEntry.getQuantity(), foundEntry.getQuantity());
        assertEquals(newEntry.getWinRate(), foundEntry.getWinRate());
        assertEquals(newEntry.getTotalSlots(), foundEntry.getTotalSlots());
        assertEquals(newEntry.getRemainingSlots(), foundEntry.getRemainingSlots());
        assertEquals(newEntry.getTitle(), foundEntry.getTitle());
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

        EventPrize newEntry = EventPrize.builder()
                .event(savedEvent)
                .itemId(1)
                .itemType(PrizeType.COUPON_TEMPLATE)
                .quantity(10)
                .winRate(new BigDecimal("0.2000"))
                .totalSlots(100)
                .remainingSlots(100)
                .title("Discount Coupon")
                .build();
        EventPrize savedEntry = eventPrizeRepository.save(newEntry);

        EventPrize modEntry = EventPrize.builder()
                .id(savedEntry.getId())
                .event(savedEvent)
                .itemId(2)
                .itemType(PrizeType.PRODUCT)
                .quantity(12)
                .winRate(new BigDecimal("0.1200"))
                .totalSlots(80)
                .remainingSlots(50)
                .title("Product Super Sale")
                .build();
        eventPrizeRepository.save(modEntry);
        EventPrize foundEntry = eventPrizeRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getEvent().getId(), foundEntry.getEvent().getId());
        assertEquals(modEntry.getItemId(), foundEntry.getItemId());
        assertEquals(modEntry.getItemType(), foundEntry.getItemType());
        assertEquals(modEntry.getQuantity(), foundEntry.getQuantity());
        assertEquals(modEntry.getWinRate(), foundEntry.getWinRate());
        assertEquals(modEntry.getTotalSlots(), foundEntry.getTotalSlots());
        assertEquals(modEntry.getRemainingSlots(), foundEntry.getRemainingSlots());
        assertEquals(modEntry.getTitle(), foundEntry.getTitle());
        assertNotNull(foundEntry.getCreatedAt());
        assertNotNull(foundEntry.getUpdatedAt());
    }

    @Test
    public void testDeleteById() {

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

        EventPrize newEntry = EventPrize.builder()
                .event(savedEvent)
                .itemId(1)
                .itemType(PrizeType.COUPON_TEMPLATE)
                .quantity(10)
                .winRate(new BigDecimal("0.2000"))
                .totalSlots(100)
                .remainingSlots(100)
                .title("Discount Coupon")
                .build();
        EventPrize savedEntry = eventPrizeRepository.save(newEntry);

        eventPrizeRepository.deleteById(savedEntry.getId());
        EventPrize foundPrize = eventPrizeRepository.findById(savedEntry.getId()).orElse(null);

        assertNull(foundPrize);
    }
}
