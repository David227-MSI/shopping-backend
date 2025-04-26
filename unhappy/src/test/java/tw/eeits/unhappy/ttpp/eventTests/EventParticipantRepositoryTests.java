package tw.eeits.unhappy.ttpp.eventTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberRepository;
import tw.eeits.unhappy.ttpp.event.enums.EventStatus;
import tw.eeits.unhappy.ttpp.event.enums.ParticipateStatus;
import tw.eeits.unhappy.ttpp.event.enums.PrizeType;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.model.EventParticipant;
import tw.eeits.unhappy.ttpp.event.model.EventPrize;
import tw.eeits.unhappy.ttpp.event.repository.EventParticipantRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventPrizeRepository;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;

@SpringBootTest
public class EventParticipantRepositoryTests {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventPrizeRepository eventPrizeRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private UserMemberRepository userMemberRepository;

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

        EventPrize prize = EventPrize.builder()
                .event(savedEvent)
                .itemId(1)
                .itemType(PrizeType.PRODUCT)
                .quantity(10)
                .winRate(new BigDecimal("0.2000"))
                .totalSlots(100)
                .remainingSlots(100)
                .title("Discount Coupon")
                .build();
        EventPrize savedPrize = eventPrizeRepository.save(prize);

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        EventParticipant newEntry = EventParticipant.builder()
                .userMember(foundUser) // to be arranged after user fk created
                .event(savedEvent)
                .eventPrize(savedPrize)
                .participateStatus(ParticipateStatus.REGISTERED)
                .build();
        EventParticipant savedEntry = eventParticipantRepository.save(newEntry);
        EventParticipant foundEntry = eventParticipantRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(1001, foundEntry.getUserMember().getId()); // to be arranged after user fk created
        assertEquals(newEntry.getEvent().getId(), foundEntry.getEvent().getId());
        assertEquals(newEntry.getEventPrize().getId(), foundEntry.getEventPrize().getId());
        assertEquals(newEntry.getParticipateStatus(), foundEntry.getParticipateStatus());
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

        EventPrize prize = EventPrize.builder()
                .event(savedEvent)
                .itemId(1)
                .itemType(PrizeType.PRODUCT)
                .quantity(10)
                .winRate(new BigDecimal("0.2000"))
                .totalSlots(100)
                .remainingSlots(100)
                .title("Discount Coupon")
                .build();
        EventPrize savedPrize = eventPrizeRepository.save(prize);

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        EventParticipant newEntry = EventParticipant.builder()
                .userMember(foundUser) // to be arranged after user fk created
                .event(savedEvent)
                .eventPrize(savedPrize)
                .participateStatus(ParticipateStatus.REGISTERED)
                .build();
        EventParticipant savedEntry = eventParticipantRepository.save(newEntry);

        EventParticipant modEntry = EventParticipant.builder()
                .id(savedEntry.getId())
                .userMember(foundUser) // to be arranged after user fk created
                .event(savedEvent)
                .eventPrize(savedPrize)
                .participateStatus(ParticipateStatus.LOST)
                .build();
        eventParticipantRepository.save(modEntry);

        EventParticipant foundEntry = eventParticipantRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getUserMember().getId(), foundEntry.getUserMember().getId());
        assertEquals(modEntry.getEvent().getId(), foundEntry.getEvent().getId());
        assertEquals(modEntry.getEventPrize().getId(), foundEntry.getEventPrize().getId());
        assertEquals(modEntry.getParticipateStatus(), foundEntry.getParticipateStatus());
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

        EventPrize prize = EventPrize.builder()
                .event(savedEvent)
                .itemId(1)
                .itemType(PrizeType.PRODUCT)
                .quantity(10)
                .winRate(new BigDecimal("0.2000"))
                .totalSlots(100)
                .remainingSlots(100)
                .title("Discount Coupon")
                .build();
        EventPrize savedPrize = eventPrizeRepository.save(prize);

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        EventParticipant newEntry = EventParticipant.builder()
                .userMember(foundUser) // to be arranged after user fk created
                .event(savedEvent)
                .eventPrize(savedPrize)
                .participateStatus(ParticipateStatus.REGISTERED)
                .build();
        EventParticipant savedEntry = eventParticipantRepository.save(newEntry);

        eventParticipantRepository.deleteById(savedEntry.getId());
        EventParticipant foundEntry = eventParticipantRepository.findById(savedEntry.getId()).orElse(null);

        assertNull(foundEntry);
    }

}
