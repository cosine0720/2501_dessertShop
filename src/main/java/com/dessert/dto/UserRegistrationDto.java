package com.dessert.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserRegistrationDto {
  @NotEmpty(message = "用戶名不能為空")
  @Size(min = 4, max = 20, message = "用戶名長度必須在4-20個字符之間")
  private String username;

  @NotEmpty(message = "密碼不能為空")
  @Size(min = 6, message = "密碼長度至少需要6個字符")
  private String password;

  @NotEmpty(message = "確認密碼不能為空")
  private String confirmPassword;

  @NotEmpty(message = "電子郵件不能為空")
  @Email(message = "請輸入有效的電子郵件地址")
  private String email;
}