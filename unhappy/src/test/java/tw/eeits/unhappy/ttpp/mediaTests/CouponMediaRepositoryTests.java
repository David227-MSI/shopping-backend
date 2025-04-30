package tw.eeits.unhappy.ttpp.mediaTests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponTemplateRepository;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.CouponMedia;
import tw.eeits.unhappy.ttpp.media.repository.CouponMediaRepository;

@SpringBootTest
public class CouponMediaRepositoryTests {
    @Autowired
    private CouponTemplateRepository couponTemplateRepository;

    @Autowired
    private CouponMediaRepository couponMediaRepository;

    @Test
    public void testSaveAndFindById() {

        CouponTemplate template = CouponTemplate.builder()
            .applicableType(ApplicableType.PRODUCT)
            .minSpend(new BigDecimal("50.00"))
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(new BigDecimal("0.10"))
            .maxDiscount(new BigDecimal("10.00"))
            .tradeable(false)
            .build();
        CouponTemplate savedTemplate = couponTemplateRepository.save(template);
        
        CouponMedia newEntry = CouponMedia.builder()
            .couponTemplate(savedTemplate)
            .mediaData("test_video".getBytes())
            .mediaType(MediaType.VIDEO)
            .build();
        CouponMedia savedEntry = couponMediaRepository.save(newEntry);
        CouponMedia foundEntry = couponMediaRepository.findById(savedEntry.getId()).orElse(null);
        
        assertNotNull(foundEntry);
        assertEquals(newEntry.getCouponTemplate().getId(), foundEntry.getCouponTemplate().getId());
        assertArrayEquals(newEntry.getMediaData(), foundEntry.getMediaData());
        assertEquals(newEntry.getMediaType(), foundEntry.getMediaType());
        assertNotNull(foundEntry.getCreatedAt());
    }

    @Test
    public void testUpdateById() {

        CouponTemplate template = CouponTemplate.builder()
            .applicableType(ApplicableType.PRODUCT)
            .minSpend(new BigDecimal("50.00"))
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(new BigDecimal("0.10"))
            .maxDiscount(new BigDecimal("10.00"))
            .tradeable(false)
            .build();
        CouponTemplate savedTemplate = couponTemplateRepository.save(template);
        
        CouponMedia newEntry = CouponMedia.builder()
            .couponTemplate(savedTemplate)
            .mediaData("test_video".getBytes())
            .mediaType(MediaType.VIDEO)
            .build();
        CouponMedia savedEntry = couponMediaRepository.save(newEntry);

        CouponMedia modEntry = CouponMedia.builder()
            .id(savedEntry.getId())
            .couponTemplate(savedTemplate)
            .mediaData("test_image".getBytes())
            .mediaType(MediaType.IMAGE)
            .build();
        couponMediaRepository.save(modEntry);

        CouponMedia foundEntry = couponMediaRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getCouponTemplate().getId(), foundEntry.getCouponTemplate().getId());
        assertArrayEquals(modEntry.getMediaData(), foundEntry.getMediaData());
        assertEquals(modEntry.getMediaType(), foundEntry.getMediaType());
    }

    @Test
    public void testDeleteById() {

        CouponTemplate template = CouponTemplate.builder()
            .applicableType(ApplicableType.PRODUCT)
            .minSpend(new BigDecimal("50.00"))
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(new BigDecimal("0.10"))
            .maxDiscount(new BigDecimal("10.00"))
            .tradeable(false)
            .build();
        CouponTemplate savedTemplate = couponTemplateRepository.save(template);
        
        CouponMedia newEntry = CouponMedia.builder()
            .couponTemplate(savedTemplate)
            .mediaData("test_video".getBytes())
            .mediaType(MediaType.VIDEO)
            .build();
        CouponMedia savedEntry = couponMediaRepository.save(newEntry);

        couponMediaRepository.deleteById(savedEntry.getId());
        CouponMedia foundEntry = couponMediaRepository.findById(savedEntry.getId()).orElse(null);

        assertNull(foundEntry);
    }





}
