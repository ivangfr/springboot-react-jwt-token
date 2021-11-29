package com.mycompany.orderapi.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class CreateOrderRequest {

    @Schema(example = "Buy two iPhones")
    @NotBlank
    private String description;
}
