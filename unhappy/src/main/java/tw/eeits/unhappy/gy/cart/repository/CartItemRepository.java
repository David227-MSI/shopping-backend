package tw.eeits.unhappy.gy.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.gy.domain.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    //判斷商品是否存在(避免重複新增)
    Optional<CartItem> findByUserMember_IdAndProduct_IdAndCheckedOutFalse(Integer userId, Integer productId);

    // 查詢未結帳商品（顯示用）
    List<CartItem> findByUserMember_IdAndCheckedOutFalse(Integer userId);

    //移除單筆使用者購物車商品
    void deleteByUserMember_IdAndProduct_Id(Integer userId, Integer productId);

    //清空使用者購物車
    void deleteByUserMember_IdAndCheckedOutFalse(Integer userId);

    //判斷是否存在(只判斷是否，效率高)
    boolean existsByUserMember_IdAndProduct_IdAndCheckedOutFalse(Integer userId, Integer productId);
}
