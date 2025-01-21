package com.dessert.controller;

import com.dessert.dto.UserRegistrationDto;
import com.dessert.security.JwtTokenUtil;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;

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
  public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
    UserDetails userDetails = userService.loadUserByUsername(username);
    if (passwordEncoder.matches(password, userDetails.getPassword())) {
      String token = jwtTokenUtil.generateToken(userDetails.getUsername());

      redirectAttributes.addFlashAttribute("token", token);
      redirectAttributes.addFlashAttribute("username", userDetails.getUsername());
      redirectAttributes.addFlashAttribute("message", "登入成功");
      return "redirect:/"; // 登入成功後跳轉到首頁或其他頁面

    } else {
      redirectAttributes.addFlashAttribute("error", "Invalid username or password");
      return "redirect:/login"; // 登入失敗後返回登入頁
    }
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
}