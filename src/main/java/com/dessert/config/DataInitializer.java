package com.dessert.config;

import com.dessert.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
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
