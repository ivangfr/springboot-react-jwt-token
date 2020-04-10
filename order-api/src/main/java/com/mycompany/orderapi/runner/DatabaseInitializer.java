package com.mycompany.orderapi.runner;

import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final OrderService orderService;

    public DatabaseInitializer(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        orders.forEach(orderService::saveOrder);
        log.info("Database initialized");
    }

    private final List<Order> orders = Collections.singletonList(
            new Order("abc", "Buy one MacBook Pro")
    );

}
