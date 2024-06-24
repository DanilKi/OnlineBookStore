package com.onlinebookstore.service;

import com.onlinebookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.model.User;

public interface ShoppingCartService {
    void registerShoppingCart(User user);

    ShoppingCartDto getByUser(String userName);

    ShoppingCartDto addCartItem(CreateCartItemRequestDto requestDto, String userName);

    ShoppingCartDto updateCartItemById(Long cartItemId, CreateCartItemRequestDto requestDto,
                                   String userName);

    void removeCartItemById(Long cartItemId, String userName);
}
