package com.mycompany.orderapi.rest;

import com.mycompany.orderapi.exception.OrderNotFoundException;
import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.rest.dto.CreateOrderDto;
import com.mycompany.orderapi.service.OrderService;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{refr}")
    public Order getOrders(@PathVariable String refr) throws OrderNotFoundException {
        return orderService.validateAndGetOrder(refr);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Order createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        return orderService.saveOrder(new Order(createOrderDto.getRefr(), createOrderDto.getDescription()));
    }

    @DeleteMapping("/{refr}")
    public Order deleteOrders(@PathVariable String refr) throws OrderNotFoundException {
        Order order = orderService.validateAndGetOrder(refr);
        orderService.deleteOrder(order);
        return order;
    }

}
