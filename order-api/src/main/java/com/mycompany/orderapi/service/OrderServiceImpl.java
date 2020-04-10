package com.mycompany.orderapi.service;

import com.mycompany.orderapi.exception.OrderNotFoundException;
import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order validateAndGetOrder(String refr) throws OrderNotFoundException {
        return orderRepository.findById(refr)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with refr %s not found", refr)));
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }
}
