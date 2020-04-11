package com.mycompany.orderapi.rest;

import com.mycompany.orderapi.service.OrderService;
import com.mycompany.orderapi.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserService userService;
    private final OrderService orderService;

    public PublicController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/numberOfUsers")
    public Integer getNumberOfUsers() {
        return userService.getUsers().size();
    }

    @GetMapping("/numberOfOrders")
    public Integer getNumberOfOrders() {
        return orderService.getOrders().size();
    }

}
