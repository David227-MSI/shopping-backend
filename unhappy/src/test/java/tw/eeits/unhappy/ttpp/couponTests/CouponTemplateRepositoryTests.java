package tw.eeits.unhappy.ttpp.couponTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.coupon.repository.CouponTemplateRepository;

@SpringBootTest
public class CouponTemplateRepositoryTests {
    
    @Autowired
    private CouponTemplateRepository couponTemplateRepository;

    @Test
    public void testSaveAndFindById() {
        CouponTemplate newEntry = CouponTemplate.builder()
            .applicableType(ApplicableType.BRAND)
            .applicableId(2)
            .minSpend(new BigDecimal("50.00"))
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(new BigDecimal("0.15"))
            .maxDiscount(new BigDecimal("20.00"))
            .tradeable(false)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .build();

        CouponTemplate savedEntry = couponTemplateRepository.save(newEntry);

        // Assert
        CouponTemplate foundEntry = couponTemplateRepository.findById(savedEntry.getId()).orElse(null);
        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(ApplicableType.BRAND, foundEntry.getApplicableType());
        assertEquals(2, foundEntry.getApplicableId());
        assertEquals(new BigDecimal("50.00"), foundEntry.getMinSpend());
        assertEquals(DiscountType.PERCENTAGE, foundEntry.getDiscountType());
        assertEquals(new BigDecimal("0.15"), foundEntry.getDiscountValue());
        assertEquals(new BigDecimal("20.00"), foundEntry.getMaxDiscount());
        assertFalse(foundEntry.getTradeable());
        assertNotNull(foundEntry.getStartTime());
        assertNotNull(foundEntry.getEndTime());
        assertNotNull(foundEntry.getCreatedAt());
        assertNull(foundEntry.getUpdatedAt());
    }

    @Test
    public void testUpdateById() {

        CouponTemplate newEntry = CouponTemplate.builder()
            .id(9)
            .applicableType(ApplicableType.PRODUCT)
            .applicableId(1)
            .minSpend(new BigDecimal("100.00"))
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(new BigDecimal("0.085"))
            .maxDiscount(new BigDecimal("30.00"))
            .tradeable(true)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .build();

        CouponTemplate savedEntry = couponTemplateRepository.save(newEntry);

        CouponTemplate foundEntry = couponTemplateRepository.findById(savedEntry.getId()).orElse(null);
        assertNotNull(foundEntry);
        System.out.println(foundEntry.getId());
        // assertEquals(9, foundEntry.getId());
    }

    @Test
    public void testDeleteById() {
        CouponTemplate newEntry = CouponTemplate.builder()
            .applicableType(ApplicableType.PRODUCT)
            .applicableId(1)
            .minSpend(new BigDecimal("100.00"))
            .discountType(DiscountType.VALUE)
            .discountValue(new BigDecimal("200"))
            .tradeable(true)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(30))
            .build();

        CouponTemplate savedEntry = couponTemplateRepository.save(newEntry);
        Integer savedEntryId = savedEntry.getId();

        couponTemplateRepository.deleteById(savedEntryId);
        savedEntry = couponTemplateRepository.findById(savedEntryId).orElse(null);

        assertNull(savedEntry);
    }



}
