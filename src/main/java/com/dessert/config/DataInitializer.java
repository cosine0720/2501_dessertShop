package com.dessert.config;

import com.dessert.entity.User;
import com.dessert.repository.UserRepository;
import com.dessert.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Bean
    CommandLineRunner initDefaultAccount() {
        return args -> {
            userService.createDefaultUsers();
        };
    }
}
