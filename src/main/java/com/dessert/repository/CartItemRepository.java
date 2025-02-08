package com.dessert.repository;

import com.dessert.entity.CartItem;
import com.dessert.entity.Product;
import com.dessert.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUser(User user);
  Optional<CartItem> findByUserAndProduct(User user, Product product);
}