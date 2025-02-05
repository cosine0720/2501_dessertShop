package com.dessert.controller;

import com.dessert.entity.User;
import com.dessert.service.CartService;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    System.out.println("viewCart======user======" + user);

    model.addAttribute("cartItems", cartService.getCartItems(user));
    return "cart";
  }

  @PostMapping("/cart/add")
  @ResponseBody
  public ResponseEntity<Map<String, String>> addToCart(@RequestParam Long productId,
      @RequestParam Integer quantity) {
    Map<String, String> response = new HashMap<>();

    try {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      User user = userService.findByUsername(username);
      cartService.addToCart(user, productId, quantity);

      response.put("status", "success");
      response.put("message", "商品已成功加入購物車");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("status", "error");
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }
}