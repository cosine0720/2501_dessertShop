package com.dessert.service;

import com.dessert.entity.CartItem;
import com.dessert.entity.Product;
import com.dessert.entity.User;
import com.dessert.repository.CartItemRepository;
import com.dessert.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;

  public void addToCart(User user, Long productId, Integer quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("商品不存在"));

    // 若購物車已存在商品
    Optional<CartItem> optionalCartItem = cartItemRepository.findByUserAndProduct(user, product);
    if (optionalCartItem.isPresent()) {
      CartItem existingCartItem = optionalCartItem.get();
      existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
      cartItemRepository.save(existingCartItem);
    } else {
      CartItem cartItem = new CartItem(user, product, quantity);
      cartItemRepository.save(cartItem);
    }
  }

  public List<CartItem> getCartItems(User user) {
    return cartItemRepository.findByUser(user);
  }

  public void updateQuantity(Long cartItemId, Integer quantity) {
    CartItem cartItem = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new RuntimeException("購物車項目不存在"));
    cartItem.setQuantity(quantity);
    cartItemRepository.save(cartItem);
  }

  public void removeFromCart(Long cartItemId) {
    cartItemRepository.deleteById(cartItemId);
  }
}