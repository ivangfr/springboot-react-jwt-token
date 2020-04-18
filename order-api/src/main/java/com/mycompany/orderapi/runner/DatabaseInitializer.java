package com.mycompany.orderapi.runner;

import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.model.User;
import com.mycompany.orderapi.security.WebSecurityConfig;
import com.mycompany.orderapi.service.OrderService;
import com.mycompany.orderapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserService userService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserService userService, OrderService orderService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userService.getUsers().isEmpty()) {
            return;
        }
        users.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
        });
        orders.forEach(orderService::saveOrder);
        log.info("Database initialized");
    }

    private final List<User> users = Arrays.asList(
            new User("admin", "admin", "Admin", "admin@mycompany.com", WebSecurityConfig.ADMIN),
            new User("user", "user", "User", "user@mycompany.com", WebSecurityConfig.USER)
    );

    private final List<Order> orders = Collections.singletonList(
            new Order("6ce8cdf5-004d-4511-a6a1-604945246af8", "Buy one MacBook Pro", users.get(0))
    );

}
