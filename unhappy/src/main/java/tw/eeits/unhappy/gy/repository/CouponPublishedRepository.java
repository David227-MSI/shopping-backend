package tw.eeits.unhappy.gy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.gy.domain.CouponPublished;

import java.util.Optional;

@Repository
public interface CouponPublishedRepository extends JpaRepository<CouponPublished, String> {
    Optional<CouponPublished> findByIdAndUserMember_Id(String couponId, Integer userId); // 查詢優惠券是否存在
}
