package com.dessert.controller;

import com.dessert.dto.UserRegistrationDto;
import com.dessert.security.JwtTokenUtil;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;
  private final JwtTokenUtil jwtTokenUtil;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
    UserDetails userDetails = userService.loadUserByUsername(username);

    if (passwordEncoder.matches(password, userDetails.getPassword())) {
      // 生成 JWT Token
      String token = jwtTokenUtil.generateToken(userDetails.getUsername());

      // 確保返回 JSON 包含 token
      Map<String, String> response = new HashMap<>();
      response.put("token", token);
      response.put("username", userDetails.getUsername());
      response.put("message", "登入成功");
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
  }

  @PostMapping("/logout")
  public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
    // 創建一個過期的 Cookie 清除 JWT Token
    Cookie jwtCookie = new Cookie("jwtToken", null);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(false);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0); // 設置 Cookie 為立即過期
    response.addCookie(jwtCookie);

    System.out.println("正在清除 jwtToken Cookie...");

    // 添加成功消息
    redirectAttributes.addFlashAttribute("message", "您已成功登出");
    return "redirect:/login"; // 跳轉至登入頁面
  }


  @GetMapping("/register")
  public String registerPage(Model model) {
    if (!model.containsAttribute("userForm")) {
      model.addAttribute("userForm", new UserRegistrationDto());
    }
    return "register";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute("userForm") UserRegistrationDto userForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    // 檢查密碼是否匹配
    if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
      bindingResult.rejectValue("confirmPassword", "error.userForm", "密碼與確認密碼不匹配");
    }
    if (userService.existsByEmail(userForm.getEmail())) {
      bindingResult.rejectValue("email", "error.userForm", "電子郵件已被使用");
    }

    if (bindingResult.hasErrors()) {
      return "register";
    }

    try {
      userService.registerUser(
          userForm.getUsername(),
          userForm.getPassword(),
          userForm.getEmail(),
              "USER");
      return "redirect:/login?registered";
    } catch (Exception e) {
      model.addAttribute("error", "註冊過程中發生錯誤，請稍後再試");
      return "register";
    }
  }

  @GetMapping("/api/user")
  @ResponseBody
  public ResponseEntity<?> getCurrentUser() {
    // 從 SecurityContext 中獲取當前用戶名
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserDetails userDetails = userService.loadUserByUsername(username);

    // 返回用戶名及角色信息
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("username", userDetails.getUsername());
    userInfo.put("roles", userDetails.getAuthorities());

    return ResponseEntity.ok(userInfo);
  }
}