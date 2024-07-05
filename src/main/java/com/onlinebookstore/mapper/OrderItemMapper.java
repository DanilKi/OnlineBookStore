package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.OrderItem;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(source = "cartItem", target = "price", qualifiedByName = "orderItemPrice")
    })
    OrderItem toOrderItemEntity(CartItem cartItem);

    @Named("orderItemPrice")
    default BigDecimal calcOrderItemPrice(CartItem cartItem) {
        return BigDecimal.valueOf(cartItem.getQuantity()).multiply(cartItem.getBook().getPrice());
    }
}
