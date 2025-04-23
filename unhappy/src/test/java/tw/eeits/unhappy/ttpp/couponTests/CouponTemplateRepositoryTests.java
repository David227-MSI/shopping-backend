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
            .startTime(LocalDateTime.parse("2025-05-21T15:00:00"))
            .endTime(LocalDateTime.parse("2025-05-21T15:00:00").plusDays(30))
            .build();

        CouponTemplate savedEntry = couponTemplateRepository.save(newEntry);

        // Assert
        CouponTemplate foundEntry = couponTemplateRepository.findById(savedEntry.getId()).orElse(null);
        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(newEntry.getApplicableType(), foundEntry.getApplicableType());
        assertEquals(newEntry.getApplicableId(), foundEntry.getApplicableId());
        assertEquals(newEntry.getMinSpend(), foundEntry.getMinSpend());
        assertEquals(newEntry.getDiscountType(), foundEntry.getDiscountType());
        assertEquals(newEntry.getDiscountValue(), foundEntry.getDiscountValue());
        assertEquals(newEntry.getMaxDiscount(), foundEntry.getMaxDiscount());
        assertEquals(newEntry.getTradeable(), foundEntry.getTradeable());
        assertEquals(newEntry.getStartTime(), foundEntry.getStartTime());
        assertEquals(newEntry.getEndTime(), foundEntry.getEndTime());
        assertNotNull(foundEntry.getCreatedAt());
        assertNull(foundEntry.getUpdatedAt());
    }

    @Test
    public void testUpdateById() {

        CouponTemplate newEntry = CouponTemplate.builder()
            .applicableType(ApplicableType.BRAND)
            .applicableId(2)
            .minSpend(new BigDecimal("50.00"))
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(new BigDecimal("0.15"))
            .maxDiscount(new BigDecimal("20.00"))
            .tradeable(false)
            .startTime(LocalDateTime.parse("2000-05-21T15:00:00"))
            .endTime(LocalDateTime.parse("2000-05-21T15:00:00").plusDays(30))
            .build();
        CouponTemplate savedEntry = couponTemplateRepository.save(newEntry);

        CouponTemplate modEntry = CouponTemplate.builder()
            .id(savedEntry.getId())
            .applicableType(ApplicableType.PRODUCT)
            .applicableId(3)
            .minSpend(new BigDecimal("500.00"))
            .discountType(DiscountType.VALUE)
            .discountValue(new BigDecimal("0.15"))
            .tradeable(false)
            .startTime(LocalDateTime.parse("2023-05-01T15:00:00"))
            .endTime(LocalDateTime.parse("2023-05-01T15:00:00").plusDays(30))
            .build();
        couponTemplateRepository.save(modEntry);

        CouponTemplate foundEntry = couponTemplateRepository.findById(savedEntry.getId()).orElse(null);
        assertNotNull(foundEntry);
        assertEquals(modEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getApplicableType(), foundEntry.getApplicableType());
        assertEquals(modEntry.getApplicableId(), foundEntry.getApplicableId());
        assertEquals(modEntry.getMinSpend(), foundEntry.getMinSpend());
        assertEquals(modEntry.getDiscountType(), foundEntry.getDiscountType());
        assertEquals(modEntry.getDiscountValue(), foundEntry.getDiscountValue());
        assertEquals(modEntry.getTradeable(), foundEntry.getTradeable());
        assertEquals(modEntry.getStartTime(), foundEntry.getStartTime());
        assertEquals(modEntry.getEndTime(), foundEntry.getEndTime());
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
            .startTime(LocalDateTime.parse("2001-05-21T15:00:00"))
            .endTime(LocalDateTime.parse("2001-05-21T15:00:00").plusDays(30))
            .build();
        CouponTemplate savedEntry = couponTemplateRepository.save(newEntry);

        couponTemplateRepository.deleteById(savedEntry.getId());
        savedEntry = couponTemplateRepository.findById(savedEntry.getId()).orElse(null);

        assertNull(savedEntry);
    }



}
