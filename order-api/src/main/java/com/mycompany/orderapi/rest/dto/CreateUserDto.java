package com.mycompany.orderapi.rest.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateUserDto {

    @ApiModelProperty(example = "user3")
    @NotBlank
    private String username;

    @ApiModelProperty(example = "user3")
    @NotBlank
    private String password;

}
