package com.ivanfranchin.orderapi.mapper;

import com.ivanfranchin.orderapi.model.Order;
import com.ivanfranchin.orderapi.rest.dto.CreateOrderRequest;
import com.ivanfranchin.orderapi.rest.dto.OrderDto;

public interface OrderMapper {

    Order toOrder(CreateOrderRequest createOrderRequest);

    OrderDto toOrderDto(Order order);
}