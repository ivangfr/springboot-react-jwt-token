package com.mycompany.orderapi.mapper;

import com.mycompany.orderapi.model.Order;
import com.mycompany.orderapi.rest.dto.CreateOrderRequest;
import com.mycompany.orderapi.rest.dto.OrderDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Configuration
@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderMapper {

  Order toOrder(CreateOrderRequest createOrderRequest);

  @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  OrderDto toOrderDto(Order order);

}