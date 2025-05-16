package tw.eeits.unhappy.ttpp.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponTemplateRepository;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.event.repository.EventRepository;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.CouponMedia;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;
import tw.eeits.unhappy.ttpp.media.repository.CouponMediaRepository;
import tw.eeits.unhappy.ttpp.media.repository.EventMediaRepository;

@Service
@RequiredArgsConstructor
public class CouponEventMediaService {
    private final CouponMediaRepository couponMediaRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final EventMediaRepository eventMediaRepository;
    private final EventRepository eventRepository;

    public void insertCouponMedia(List<String> paths, List<Integer> templateIds) throws IOException {

        if (paths.size() != templateIds.size()) {
            throw new IllegalArgumentException("Paths list size must be equal to templateIds list size.");
        }

        for (int i = 0; i < paths.size(); i++) {
            String imagePath = paths.get(i);
            Integer templateId = templateIds.get(i);

            CouponTemplate foundTemplate = couponTemplateRepository.findById(templateId).orElse(null);

            if (foundTemplate != null) {
                Path path = Paths.get(imagePath);
                byte[] imageData = Files.readAllBytes(path);
                CouponMedia newEntry = CouponMedia.builder()
                        .couponTemplate(foundTemplate)
                        .mediaType(MediaType.IMAGE)
                        .mediaData(imageData)
                        .build();
                couponMediaRepository.save(newEntry);
            } else {
                throw new IllegalArgumentException("Template with ID " + templateId + " not found.");
            }
        }
    }

    public void insertEventMedia(List<String> paths, List<Integer> eventIds) throws IOException {
        if (paths.size() != eventIds.size()) {
            throw new IllegalArgumentException("Paths list size must be equal to eventIds list size.");
        }

        for (int i = 0; i < paths.size(); i++) {
            String imagePath = paths.get(i);
            Integer eventId = eventIds.get(i);

            Event foundEvent = eventRepository.findById(eventId).orElse(null);

            if (foundEvent != null) {
                Path path = Paths.get(imagePath);
                byte[] imageData = Files.readAllBytes(path);
                EventMedia newEntry = EventMedia.builder()
                        .event(foundEvent)
                        .mediaType(MediaType.IMAGE)
                        .mediaData(imageData)
                        .build();
                eventMediaRepository.save(newEntry);
            } else {
                throw new IllegalArgumentException("Event with ID " + eventIds + " not found.");
            }
        }
    }

}
