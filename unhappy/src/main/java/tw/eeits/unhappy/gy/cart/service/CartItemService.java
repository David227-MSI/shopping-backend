package tw.eeits.unhappy.gy.cart.service;



import tw.eeits.unhappy.gy.dto.CartItemRequestDTO;
import tw.eeits.unhappy.gy.dto.CartItemResponseDTO;

import java.util.List;

public interface CartItemService {

    // 新增或者更新購物車
    void addItem(CartItemRequestDTO dto);

    // 查詢使用者購物車內容
    List<CartItemResponseDTO> getCartItemsByUserId(Integer userId);

    // 修改購物車商品數量
    void updateItemQuantity(CartItemRequestDTO dto);

    // 移除購物車商品
    void removeItem(Integer userId, Integer productId);

    // 清空購物車
    void clearCart(Integer userId);
}
