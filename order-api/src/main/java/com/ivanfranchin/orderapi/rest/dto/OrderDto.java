package com.ivanfranchin.orderapi.rest.dto;

import java.time.Instant;

import com.ivanfranchin.orderapi.order.Order;

public record OrderDto(String id, String description, UserDto user, Instant createdAt) {

  public record UserDto(String username) {}

  public static OrderDto from(Order order) {
    UserDto userDto = new UserDto(order.getUser().getUsername());
    return new OrderDto(order.getId(), order.getDescription(), userDto, order.getCreatedAt());
  }
}
