package tw.eeits.unhappy.ttpp.couponTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp._fake.UserMember;
import tw.eeits.unhappy.ttpp._fake.UserMemberRepository;
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

    @Autowired
    private UserMemberRepository userMemberRepository;


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

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        CouponPublished newEntry = CouponPublished.builder()
            .couponTemplate(savedTemplate)
            .userMember(foundUser)
            .isUsed(false)
            .build();
        CouponPublished savedEntry = couponPublishedRepository.save(newEntry);

        CouponPublished foundEntry = couponPublishedRepository.findById(newEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(savedEntry.getId(), foundEntry.getId());
        assertEquals(savedTemplate.getId(), foundEntry.getCouponTemplate().getId());
        assertEquals(newEntry.getUserMember(), foundEntry.getUserMember()); // to be arranged after user fk created
        assertEquals(newEntry.getIsUsed(), foundEntry.getIsUsed());
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

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        CouponPublished newEntry = CouponPublished.builder()
            .couponTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .isUsed(false)
            .build();
        CouponPublished savedEntry = couponPublishedRepository.save(newEntry);
        
        CouponPublished modEntry = CouponPublished.builder()
            .id(savedEntry.getId())
            .couponTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .build();
        couponPublishedRepository.save(modEntry);

        CouponPublished foundEntry = couponPublishedRepository.findById(savedEntry.getId()).orElse(null);

        assertNotNull(foundEntry);
        assertEquals(modEntry.getId(), foundEntry.getId());
        assertEquals(modEntry.getCouponTemplate().getId(), foundEntry.getCouponTemplate().getId());
        assertEquals(modEntry.getUserMember(), foundEntry.getUserMember()); // to be arranged after user fk created
        assertEquals(modEntry.getIsUsed(), foundEntry.getIsUsed());
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

        UserMember foundUser = userMemberRepository.findById(1001).orElse(null);

        CouponPublished newEntry = CouponPublished.builder()
            .couponTemplate(savedTemplate)
            .userMember(foundUser) // to be arranged after user fk created
            .isUsed(false)
            .build();
        CouponPublished savedEntry = couponPublishedRepository.save(newEntry);
        couponPublishedRepository.deleteById(savedEntry.getId());

        CouponPublished foundEntry = couponPublishedRepository.findById(savedEntry.getId()).orElse(null);

        assertNull(foundEntry);
    }

}
