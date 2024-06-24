package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.mapper.CartItemMapper;
import com.onlinebookstore.mapper.ShoppingCartMapper;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.shoppingcart.CartItemRepository;
import com.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import com.onlinebookstore.service.ShoppingCartService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public void registerShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto getByUser(String userName) {
        ShoppingCart shoppingCart = getShoppingCart(userName);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addCartItem(CreateCartItemRequestDto requestDto, String userName) {
        if (!bookRepository.existsById(requestDto.getBookId())) {
            throw new EntityNotFoundException("Book with id: " + requestDto.getBookId()
                    + " doesn't exist in DB");
        }
        ShoppingCart shoppingCart = getShoppingCart(userName);
        if (shoppingCart.getCartItems().stream()
                .anyMatch(cartItem -> cartItem.getBook().getId().equals(requestDto.getBookId()))) {
            throw new EntityExistsException("Cart item with bookId: " + requestDto.getBookId()
                    + " already exist in the shopping cart");
        }
        CartItem cartItem = cartItemMapper.toCartItemEntity(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toShoppingCartDto(getShoppingCart(userName));
    }

    @Override
    public ShoppingCartDto updateCartItemById(Long cartItemId, CreateCartItemRequestDto requestDto,
                                              String userName) {
        ShoppingCart shoppingCart = getShoppingCart(userName);
        CartItem cartItem = getCartItem(cartItemId, shoppingCart.getId(), userName);
        cartItemMapper.toCartItemEntity(requestDto, cartItem);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toShoppingCartDto(getShoppingCart(userName));
    }

    @Override
    public void removeCartItemById(Long cartItemId, String userName) {
        ShoppingCart shoppingCart = getShoppingCart(userName);
        CartItem cartItem = getCartItem(cartItemId, shoppingCart.getId(), userName);
        cartItemRepository.delete(cartItem);
    }

    private ShoppingCart getShoppingCart(String userName) {
        return shoppingCartRepository.findByUserEmail(userName).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart for user: " + userName)
        );
    }

    private CartItem getCartItem(Long cartItemId, Long shoppingCartId, String userName) {
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCartId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find cart item by id: " + cartItemId
                        + " for user: " + userName)
        );
    }
}
