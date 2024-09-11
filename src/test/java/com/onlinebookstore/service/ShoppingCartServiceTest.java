package com.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.shoppingcart.CartItemDto;
import com.onlinebookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.mapper.CartItemMapper;
import com.onlinebookstore.mapper.ShoppingCartMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.shoppingcart.CartItemRepository;
import com.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import com.onlinebookstore.service.impl.ShoppingCartServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper cartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl cartService;
    private ShoppingCart shoppingCart;
    private CartItem cartItem;
    private ShoppingCartDto shoppingCartDto;
    private CartItemDto cartItemDto;
    private CreateCartItemRequestDto requestDto;
    private final String userName = "admin@gmail.com";

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setEmail(userName);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(new Book(1L));
        cartItem.setQuantity(1);

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(cartItem));

        cartItemDto = new CartItemDto(1L, 1L, "Sample Book 1", 1);

        shoppingCartDto = new ShoppingCartDto(1L,1L, List.of(cartItemDto));

        requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(1);
    }

    @Test
    @DisplayName("""
            Get existing user's shopping cart
            """)
    void getByUser_UserNameExists_ReturnsValidShoppingCartDto() {
        ShoppingCartDto expected = shoppingCartDto;
        when(cartRepository.findByUserEmail(userName)).thenReturn(Optional.of(shoppingCart));
        when(cartMapper.toShoppingCartDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actual = cartService.getByUser(userName);

        assertEquals(expected, actual);
        verify(cartRepository, times(1)).findByUserEmail(userName);
    }

    @Test
    @DisplayName("""
            Add new cart item to user's shopping cart
            """)
    void addCartItem_ValidCreateCartItemRequestDto_ReturnsValidShoppingCartDto() {
        shoppingCart.setCartItems(Collections.emptySet());
        when(bookRepository.existsById(requestDto.getBookId())).thenReturn(true);
        when(cartRepository.findByUserEmail(userName)).thenReturn(Optional.of(shoppingCart));
        when(cartItemMapper.toCartItemEntity(requestDto)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartMapper.toShoppingCartDto(shoppingCart)).thenReturn(shoppingCartDto);
        ShoppingCartDto expected = shoppingCartDto;

        ShoppingCartDto actual = cartService.addCartItem(requestDto, userName);

        assertEquals(expected, actual);
        verify(cartRepository, times(2)).findByUserEmail(userName);
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    @DisplayName("""
            Update existing cart item in user's shopping cart
            """)
    void updateCartItemById_CartItemWithIdExists_ReturnsUpdatedShoppingCartDto() {
        Long cartItemId = 1L;
        when(cartRepository.findByUserEmail(userName)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemMapper).toCartItemEntity(requestDto, cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartMapper.toShoppingCartDto(shoppingCart)).thenReturn(shoppingCartDto);
        ShoppingCartDto expected = shoppingCartDto;

        ShoppingCartDto actual = cartService.updateCartItemById(cartItemId, requestDto, userName);

        assertEquals(expected, actual);
        verify(cartRepository, times(2)).findByUserEmail(userName);
        verify(cartItemRepository, times(1)).findByIdAndShoppingCartId(anyLong(), anyLong());
        verify(cartItemRepository, times(1)).save(cartItem);
    }
}
