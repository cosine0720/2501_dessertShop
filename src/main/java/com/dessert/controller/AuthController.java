package com.dessert.controller;

import com.dessert.dto.UserRegistrationDto;
import com.dessert.security.JwtUtil;
import com.dessert.entity.User;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
    try {
      User user = userService.findByUsername(username);

      if (!passwordEncoder.matches(password, user.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
      }

      UserDetails userDetails = userService.loadUserByUsername(username);

      // 生成 JWT Token
      String token = jwtUtil.generateToken(userDetails.getUsername());

      // 返回 JSON
      Map<String, String> response = new HashMap<>();
      response.put("token", token);
      response.put("username", userDetails.getUsername());
      response.put("message", "登入成功");

      return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
  }

  @PostMapping("/logout")
  public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
    // 清除 JWT Token 的 Cookie
    Cookie jwtCookie = new Cookie("jwtToken", null);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setSecure(false);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0); // 設置 Cookie 為立即過期
    response.addCookie(jwtCookie);

    System.out.println("正在清除 jwtToken Cookie...");

    // 顯示登出成功訊息
    redirectAttributes.addFlashAttribute("message", "您已成功登出");
    return "redirect:/login";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    if (!model.containsAttribute("userForm")) {
      model.addAttribute("userForm", new UserRegistrationDto());
    }
    return "register"; // 這將顯示 `register.html`
  }

  @PostMapping("/register")
  public String register(
          @Valid @ModelAttribute("userForm") UserRegistrationDto userForm,
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
              "USER"); // 預設角色為 USER

      return "redirect:/login?registered"; // 註冊成功後導向登入頁
    } catch (Exception e) {
      model.addAttribute("error", "註冊過程中發生錯誤，請稍後再試");
      return "register";
    }
  }

  @GetMapping("/api/user")
  @ResponseBody
  public ResponseEntity<?> getCurrentUser() {
    // 從 SecurityContext 取得目前用戶名稱
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserDetails userDetails = userService.loadUserByUsername(username);

    // 回傳 JSON
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("username", userDetails.getUsername());
    userInfo.put("roles", userDetails.getAuthorities());

    return ResponseEntity.ok(userInfo);
  }
}
