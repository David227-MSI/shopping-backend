package tw.eeits.unhappy.gy.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.eeits.unhappy.gy.cart.service.CartItemService;
import tw.eeits.unhappy.gy.dto.CartItemRequestDTO;
import tw.eeits.unhappy.gy.dto.CartItemResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    // 加入
    @PostMapping
    public ResponseEntity<String> addItemToCart(@RequestBody CartItemRequestDTO dto) {
        cartItemService.addItem(dto);
        return ResponseEntity.ok("商品已成功加入購物車");

    }

    // 查詢
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponseDTO>> getCartItemsByUserId(@PathVariable Integer userId) {
        List<CartItemResponseDTO> cartItems = cartItemService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    // 更新
    @PutMapping
    public ResponseEntity<String> updateCartItem(@RequestBody CartItemRequestDTO dto) {
        cartItemService.updateItemQuantity(dto);
        return ResponseEntity.ok("商品數量已更新");
    }

    //移除
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartItemService.removeItem(userId, productId);
        return ResponseEntity.ok("商品已從購物車移除");
    }

    //移除購物車全部商品
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Integer userId) {
        cartItemService.clearCart(userId);
        return ResponseEntity.ok("購物車已清空");
    }

}


