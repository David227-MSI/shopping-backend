package tw.eeits.unhappy.gy.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.eeits.unhappy.gy.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
