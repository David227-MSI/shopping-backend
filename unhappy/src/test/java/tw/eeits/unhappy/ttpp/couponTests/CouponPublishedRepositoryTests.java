package tw.eeits.unhappy.ttpp.couponTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponPublishedRepository;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponTemplateRepository;

@SpringBootTest
public class CouponPublishedRepositoryTests {
    @Autowired
    private CouponPublishedRepository couponPublishedRepository;

    @Autowired
    private CouponTemplateRepository couponTemplateRepository;


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

        CouponPublished newEntry = CouponPublished.builder()
            .couponTemplate(savedTemplate)
            .userId(1001) // to be arranged after user fk created
            .isUsed(false)
            .build();

        CouponPublished savedEntry = couponPublishedRepository.save(newEntry);

        CouponPublished foundEntry = couponPublishedRepository.findById(newEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(savedTemplate.getId(), foundEntry.getCouponTemplate().getId());
        assertEquals(1001, foundEntry.getUserId()); // to be arranged after user fk created
        assertFalse(foundEntry.getIsUsed());
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

        CouponPublished newEntry = CouponPublished.builder()
            .couponTemplate(savedTemplate)
            .userId(1001) // to be arranged after user fk created
            .isUsed(false)
            .build();

        CouponPublished savedEntry = couponPublishedRepository.save(newEntry);
        String savedEntryId = savedEntry.getId();

        CouponPublished modEntry = CouponPublished.builder()
            .id(savedEntryId)
            .couponTemplate(savedTemplate)
            .userId(1002) // to be arranged after user fk created
            .build();

        savedEntry = couponPublishedRepository.save(modEntry);
        CouponPublished foundEntry = couponPublishedRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(savedTemplate.getId(), foundEntry.getCouponTemplate().getId());
        assertEquals(1002, foundEntry.getUserId()); // to be arranged after user fk created
        assertFalse(foundEntry.getIsUsed());
        assertNotNull(foundEntry.getCreatedAt());
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

        CouponPublished newEntry = CouponPublished.builder()
            .couponTemplate(savedTemplate)
            .userId(1003) // to be arranged after user fk created
            .isUsed(false)
            .build();

        CouponPublished savedEntry = couponPublishedRepository.save(newEntry);
        couponPublishedRepository.deleteById(savedEntry.getId());
    }

}
