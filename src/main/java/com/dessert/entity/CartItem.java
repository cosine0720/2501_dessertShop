package com.dessert.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false) // 外鍵列名設為 user_id
  private User user;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false) // 外鍵列名設為 product_id
  private Product product;

  private Integer quantity;

  public CartItem(User user, Product product, Integer quantity) {
    this.user = user;
    this.product = product;
    this.quantity = quantity;
  }
}