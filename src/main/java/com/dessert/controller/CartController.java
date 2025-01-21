package com.dessert.controller;

import com.dessert.entity.User;
import com.dessert.service.CartService;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;
  private final UserService userService;

  @GetMapping("/cart")
  public String viewCart(Model model, @AuthenticationPrincipal User user) {
    model.addAttribute("cartItems", cartService.getCartItems(user));
    return "cart";
  }

  @PostMapping("/cart/add")
  @ResponseBody
  public String addToCart(@RequestParam Long productId,
      @RequestParam Integer quantity) {
    try {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      User user = userService.findByUsername(username);
      cartService.addToCart(user, productId, quantity);
      return "success";

    } catch (Exception e) {
      return "error: " + e.getMessage();
    }
  }
}