package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.UpdateOrderRequestDto;
import com.onlinebookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toOrderDto(Order order);

    Order toOrderEntity(CreateOrderRequestDto requestDto);

    void toOrderEntity(UpdateOrderRequestDto requestDto, @MappingTarget Order order);
}
