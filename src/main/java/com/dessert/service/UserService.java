package com.dessert.service;

import com.dessert.entity.User;
import com.dessert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 直接返回我們的 User 實體
    User user = findByUsername(username);
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .roles("USER")
        .build();
  }

  public User registerUser(String username, String password, String email, String role) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmail(email);
    user.setRole(role);
    return userRepository.save(user);
  }

  public void createDefaultUsers() {
    // 檢查是否存在 admin 用戶，如果不存在則創建
    if (!existsByUsername("admin")) {
      registerUser("admin", "admin123", "admin@example.com", "ADMIN");
    }
    System.out.println("Admin account created: admin/admin123");

    // 檢查是否存在 user1 用戶，如果不存在則創建
    if (!existsByUsername("user1")) {
      registerUser("user1", "user111", "user1@example.com", "USER");
    }
    System.out.println("User account created: user1/user111");
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用戶不存在: " + username));
  }

  public boolean existsByUsername(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  public boolean existsByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }
}