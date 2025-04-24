package tw.eeits.unhappy.gy.order.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.gy.domain.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // 解決 LAZY loading (查明細)
    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findWithItemsById(@Param("id") Integer id);

    // 查會員所有訂單 (依時間排序，從最新到最舊)
    List<Order> findByUserMember_IdOrderByCreatedAtDesc(Integer userId);
}
