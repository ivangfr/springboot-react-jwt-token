package com.mycompany.orderapi.mapper;

import com.mycompany.orderapi.model.User;
import com.mycompany.orderapi.rest.dto.UserDto;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);
}