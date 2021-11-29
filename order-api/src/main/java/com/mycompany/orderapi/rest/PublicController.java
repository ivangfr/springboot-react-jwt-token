package com.mycompany.orderapi.rest;

import com.mycompany.orderapi.service.OrderService;
import com.mycompany.orderapi.service.UserService;
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
    public Integer getNumberOfUsers() {
        return userService.getUsers().size();
    }

    @GetMapping("/numberOfOrders")
    public Integer getNumberOfOrders() {
        return orderService.getOrders().size();
    }
}
