package com.dessert.controller;

import com.dessert.dto.UserRegistrationDto;
import com.dessert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
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