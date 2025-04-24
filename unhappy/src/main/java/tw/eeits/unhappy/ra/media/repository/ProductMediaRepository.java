package tw.eeits.unhappy.ra.media.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import tw.eeits.unhappy.ra.media.model.ProductMedia;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, Integer> {

    // 取商品全部 media，並鎖行避免並發衝突
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ProductMedia> findByProductId(Integer productId);

    // 取主圖（可給前端顯示）
    Optional<ProductMedia> findFirstByProductIdAndIsMainTrue(Integer productId);

    // 產生 mediaOrder 用
    long countByProductId(Integer productId);
}
