package com.dessert.service;

import com.dessert.entity.CartItem;
import com.dessert.entity.Product;
import com.dessert.entity.User;
import com.dessert.repository.CartItemRepository;
import com.dessert.repository.ProductRepository;
import com.dessert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  public void addToCart(String username, Long productId, int quantity) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用戶不存在"));
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("找不到商品"));

    Optional<CartItem> optionalCartItem = cartItemRepository.findByUserAndProduct(user, product);
    // 若購物車已存在商品
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

  public void removeCartItem(String username, Long cartItemId) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("用戶不存在"));
    cartItemRepository.findById(cartItemId).ifPresent(cartItem -> {
      if (cartItem.getUser().equals(user)) {
        cartItemRepository.delete(cartItem);
      }
    });
  }
}