package tw.eeits.unhappy.gy.order.service;


import tw.eeits.unhappy.gy.dto.OrderDetailResponseDTO;
import tw.eeits.unhappy.gy.dto.OrderRequestDTO;
import tw.eeits.unhappy.gy.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO dto);
    OrderDetailResponseDTO getOrderDetail(Integer orderId);
    List<OrderResponseDTO> getOrdersByUser(Integer userId);
}
