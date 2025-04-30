package tw.eeits.unhappy.gy.cart.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.gy.cart.repository.CartItemRepository;
import tw.eeits.unhappy.gy.domain.CartItem;
import tw.eeits.unhappy.gy.dto.CartItemRequestDTO;
import tw.eeits.unhappy.gy.dto.CartItemResponseDTO;
import tw.eeits.unhappy.gy.exception.CartItemNotFoundException;
import tw.eeits.unhappy.gy.exception.InvalidQuantityException;
import tw.eeits.unhappy.gy.exception.ProductNotFoundException;
import tw.eeits.unhappy.gy.exception.UserNotFoundException;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserMemberRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 計算小計(提出)
    private BigDecimal calculateSubtotal(Product product, Integer quantity) {
        return product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }

    // 加入購物車
    @Override
    public void addItem(CartItemRequestDTO dto) {
        CartItem item = cartItemRepository
                .findByUserMember_IdAndProduct_IdAndCheckedOutFalse(dto.getUserId(), dto.getProductId())
                .orElse(null);

        if (item != null) {
            if (dto.getQuantity() <= 0) throw new InvalidQuantityException(dto.getQuantity());
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            cartItemRepository.save(item);
        } else {
            UserMember user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(dto.getProductId()));

            CartItem newItem = CartItem.builder()
                    .userMember(user)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .checkedOut(false)
                    .build();

            cartItemRepository.save(newItem);
        }
    }

    // 查詢某使用者的購物車
    @Override
    public List<CartItemResponseDTO> getCartItemsByUserId(Integer userId) {
        return cartItemRepository.findByUserMember_IdAndCheckedOutFalse(userId)
                .stream()
                .map(item -> CartItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getProduct().getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(calculateSubtotal(item.getProduct(), item.getQuantity()))
                        .checkedOut(item.getCheckedOut())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 修改購物車數量
    @Override
    public void updateItemQuantity(CartItemRequestDTO dto) {
        CartItem item = cartItemRepository
                .findByUserMember_IdAndProduct_IdAndCheckedOutFalse(dto.getUserId(), dto.getProductId())
                .orElseThrow(() -> new CartItemNotFoundException(dto.getUserId(), dto.getProductId()));

        if (dto.getQuantity() <= 0) throw new InvalidQuantityException(dto.getQuantity());
        item.setQuantity(dto.getQuantity());
        cartItemRepository.save(item);
    }

    // 移除購物車產品
    @Override
    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        if (!cartItemRepository.existsByUserMember_IdAndProduct_IdAndCheckedOutFalse(userId, productId)) {
            throw new CartItemNotFoundException(userId, productId);
        }
        cartItemRepository.deleteByUserMember_IdAndProduct_Id(userId, productId);
    }

    // 清空購物車
    @Override
    @Transactional
    public void clearCart(Integer userId) {
        cartItemRepository.deleteByUserMember_IdAndCheckedOutFalse(userId);
    }
}
