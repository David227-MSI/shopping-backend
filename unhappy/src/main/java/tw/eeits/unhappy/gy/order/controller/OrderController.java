package tw.eeits.unhappy.gy.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.eeits.unhappy.gy.dto.OrderDetailResponseDTO;
import tw.eeits.unhappy.gy.dto.OrderRequestDTO;
import tw.eeits.unhappy.gy.dto.OrderResponseDTO;
import tw.eeits.unhappy.gy.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 建立訂單
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO dto) {
        OrderResponseDTO order = orderService.createOrder(dto);
        return ResponseEntity.ok(order);
    }

    // 查詢某會員的所有訂單
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrder(@PathVariable Integer userId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    // 查詢單筆訂單明細
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponseDTO> getOrderDetail(@PathVariable("id") Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }
}
