package com.ivanfranchin.orderapi.rest;

import com.ivanfranchin.orderapi.order.OrderService;
import com.ivanfranchin.orderapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/numberOfUsers")
    public long getNumberOfUsers() {
        return userService.countUsers();
    }

    @GetMapping("/numberOfOrders")
    public long getNumberOfOrders() {
        return orderService.countOrders();
    }
}
