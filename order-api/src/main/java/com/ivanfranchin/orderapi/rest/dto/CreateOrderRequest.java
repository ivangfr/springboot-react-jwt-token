package com.ivanfranchin.orderapi.rest.dto;

import jakarta.validation.constraints.NotBlank;

import com.ivanfranchin.orderapi.order.Order;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateOrderRequest(
    @Schema(example = "Buy two iPhones") @NotBlank String description) {

  public Order toDomain() {
    return new Order(description);
  }
}
