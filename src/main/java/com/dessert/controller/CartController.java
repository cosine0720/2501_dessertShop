package com.dessert.controller;

import com.dessert.entity.User;
import com.dessert.service.CartService;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;
  private final UserService userService;

  @GetMapping("/cart")
  public String viewCart(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByUsername(username);

    model.addAttribute("cartItems", cartService.getCartItems(user));
    return "cart";
  }

  @GetMapping("/cart/content")
  public String viewCartContent(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByUsername(username);

    model.addAttribute("cartItems", cartService.getCartItems(user));
    return "fragments/cart-content";  // 只返回購物車內容
  }


  @PostMapping("/cart/add")
  @ResponseBody
  public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload, Principal principal) {
    System.out.println("productId......" + payload.get("productId"));
    System.out.println("quantity......" + payload.get("quantity"));

    // 確保 Principal 正確解析
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
    }

    // 確保 productId 和 quantity 存在
    if (!payload.containsKey("productId") || !payload.containsKey("quantity")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "請提供商品 ID 和數量"));
    }

    // 確保數據類型正確
    try {
      Long productId = Long.parseLong(payload.get("productId").toString());
      int quantity = Integer.parseInt(payload.get("quantity").toString());
      String username = principal.getName();

      cartService.addToCart(username, productId, quantity);
      return ResponseEntity.ok(Map.of("message", "商品已加入購物車"));
    } catch (NumberFormatException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "數據格式不正確"));
    }
  }
}