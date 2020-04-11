package com.mycompany.orderapi.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

@Data
public class JwtRequest {

    @ApiModelProperty(example = "user")
    @NotBlank
    private String username;

    @ApiModelProperty(position = 1, example = "user")
    @NotBlank
    private String password;

}
