package tw.eeits.unhappy.gy.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.eeits.unhappy.gy.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
