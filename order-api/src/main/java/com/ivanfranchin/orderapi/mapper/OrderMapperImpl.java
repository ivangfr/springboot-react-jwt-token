package com.ivanfranchin.orderapi.mapper;

import com.ivanfranchin.orderapi.model.Order;
import com.ivanfranchin.orderapi.rest.dto.CreateOrderRequest;
import com.ivanfranchin.orderapi.rest.dto.OrderDto;
import org.springframework.stereotype.Service;

@Service
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toOrder(CreateOrderRequest createOrderRequest) {
        return new Order(createOrderRequest.getDescription());
    }

    @Override
    public OrderDto toOrderDto(Order order) {
        OrderDto.UserDto userDto = new OrderDto.UserDto(order.getUser().getUsername());
        return new OrderDto(order.getId(), order.getDescription(), userDto, order.getCreatedAt());
    }
}
