package com.dessert.controller;

import com.dessert.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @GetMapping("/products/{id}")
  public String productDetail(@PathVariable Long id, Model model) {
    model.addAttribute("product", productService.getProductById(id));
    return "product-detail";
  }
}