package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.shoppingcart.CartItemDto;
import com.onlinebookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.onlinebookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mappings({
            @Mapping(source = "book.id", target = "bookId"),
            @Mapping(source = "book.title", target = "bookTitle")
    })
    CartItemDto toCartItemDto(CartItem cartItem);

    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toCartItemEntity(CreateCartItemRequestDto requestDto);

    void toCartItemEntity(CreateCartItemRequestDto requestDto, @MappingTarget CartItem cartItem);
}
