package com.mycompany.orderapi.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRequest {

    @Schema(example = "Buy two iPhones")
    @NotBlank
    private String description;
}
