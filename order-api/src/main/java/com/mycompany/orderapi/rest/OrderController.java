package com.mycompany.orderapi.rest;

import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.model.User;
import com.mycompany.orderapi.rest.dto.CreateOrderDto;
import com.mycompany.orderapi.security.CustomUserDetails;
import com.mycompany.orderapi.service.OrderService;
import com.mycompany.orderapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public Order getOrders(@PathVariable UUID id) {
        return orderService.validateAndGetOrder(id.toString());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Order createOrder(@AuthenticationPrincipal CustomUserDetails currentUser, @Valid @RequestBody CreateOrderDto createOrderDto) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        return orderService.saveOrder(new Order(UUID.randomUUID().toString(), createOrderDto.getDescription(), user));
    }

    @DeleteMapping("/{id}")
    public Order deleteOrders(@PathVariable UUID id) {
        Order order = orderService.validateAndGetOrder(id.toString());
        orderService.deleteOrder(order);
        return order;
    }

}
