package tw.eeits.unhappy.gy.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.eeits.unhappy.gy.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // 月銷報表要用的
    List<OrderItem> findByOrderIdIn(List<Integer> orderIds);
}
