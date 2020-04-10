package com.mycompany.orderapi.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderDto {

    @ApiModelProperty(example = "ghi")
    @NotBlank
    private String refr;

    @ApiModelProperty(position = 1, example = "Buy two iPhones")
    @NotBlank
    private String description;

}
