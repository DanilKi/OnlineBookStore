package com.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.UpdateOrderRequestDto;
import com.onlinebookstore.mapper.OrderItemMapper;
import com.onlinebookstore.mapper.OrderMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.Status;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.order.OrderItemRepository;
import com.onlinebookstore.repository.order.OrderRepository;
import com.onlinebookstore.service.impl.OrderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private ShoppingCartService cartService;
    @Mock
    private UserService userService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @InjectMocks
    private OrderServiceImpl orderService;
    private Order order;
    private OrderItem orderItem;
    private OrderDto orderDto;
    private OrderItemDto orderItemDto;
    private final String userName = "admin@gmail.com";

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setEmail(userName);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(new Book(1L));
        orderItem.setPrice(BigDecimal.valueOf(19.99));
        orderItem.setQuantity(1);

        order = new Order();
        order.setUser(user);
        order.setId(1L);
        order.setTotal(BigDecimal.valueOf(19.99));
        order.setShippingAddress("Kyiv, Main str, 10");
        order.setOrderItems(Set.of(orderItem));

        orderItemDto = new OrderItemDto(1L, 1L, 1);

        orderDto = new OrderDto(1L, 1L, order.getOrderDate(),
                BigDecimal.valueOf(19.99), Status.PENDING, List.of(orderItemDto));
    }

    @Test
    @DisplayName("""
            Create new order and save it to DB
            """)
    void save_ValidCreateOrderRequestDto_ReturnsValidOrderDto() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(new Book(1L));
        cartItem.setQuantity(1);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(order.getUser());
        shoppingCart.getCartItems().add(cartItem);
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto("Kyiv, Main str, 10");
        when(cartService.getShoppingCart(userName)).thenReturn(shoppingCart);
        when(orderMapper.toOrderEntity(requestDto)).thenReturn(order);
        when(orderItemMapper.toOrderItemEntity(cartItem)).thenReturn(orderItem);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        OrderDto expected = orderDto;

        OrderDto actual = orderService.save(requestDto, userName);

        assertEquals(expected, actual);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("""
            Find all existing orders by user's name
            """)
    void findAllByUser_OrdersPresentInDB_ReturnsListOfOrderDto() {
        when(userService.getUserByEmail(userName)).thenReturn(order.getUser());
        when(orderRepository.findAllByUserEmail(userName, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(order)));
        when(orderMapper.toOrderDto(order)).thenReturn(orderDto);
        List<OrderDto> expected = List.of(orderDto);

        List<OrderDto> actual = orderService.findAllByUser(Pageable.unpaged(), userName);

        assertEquals(expected, actual);
        verify(orderRepository, times(1)).findAllByUserEmail(userName, Pageable.unpaged());
    }

    @Test
    @DisplayName("""
            Find order items by existing order id in DB
            """)
    void findOrderItemsByOrderId_OrderWithIdExists_ReturnsOneOrderItemDto() {
        List<OrderItemDto> expected = List.of(orderItemDto);
        Long orderId = 1L;
        when(orderRepository.findByIdAndUserEmail(orderId, userName))
                .thenReturn(Optional.of(order));
        when(orderItemMapper.toOrderItemDto(orderItem)).thenReturn(orderItemDto);

        List<OrderItemDto> actual = orderService.findOrderItemsByOrderId(orderId, userName);

        assertEquals(expected, actual);
        verify(orderRepository, times(1)).findByIdAndUserEmail(orderId, userName);
    }

    @Test
    @DisplayName("""
            Find order items by non-existing order id in DB
            """)
    void findOrderItemsByOrderId_OrderWithIdNotExists_ThrowsException() {
        when(orderRepository.findByIdAndUserEmail(1L, userName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.findOrderItemsByOrderId(1L, userName));
        verify(orderRepository, times(1)).findByIdAndUserEmail(anyLong(), anyString());
    }

    @Test
    @DisplayName("""
            Find existing order item by id and order id in DB
            """)
    void findOrderItemById_OrderItemAndOrderWithIdExist_ReturnsOneOrderItemDto() {
        Long orderId = 1L;
        Long orderItemId = 1L;
        OrderItemDto expected = orderItemDto;
        when(orderItemRepository.findByIdAndOrderIdAndOrderUserEmail(
                orderItemId, orderId, userName)).thenReturn(Optional.of(orderItem)
        );
        when(orderItemMapper.toOrderItemDto(orderItem)).thenReturn(orderItemDto);

        OrderItemDto actual = orderService.findOrderItemById(orderId, orderItemId, userName);

        assertEquals(expected, actual);
        verify(orderItemRepository, times(1))
                .findByIdAndOrderIdAndOrderUserEmail(orderItemId, orderId, userName);
    }

    @Test
    @DisplayName("""
            Find non-existing order item by id and order id in DB
            """)
    void findOrderItemById_OrderItemAndOrderWithIdNotExist_ThrowsException() {
        when(orderItemRepository.findByIdAndOrderIdAndOrderUserEmail(1L, 1L, userName))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.findOrderItemById(1L, 1L, userName));
        verify(orderItemRepository, times(1))
                .findByIdAndOrderIdAndOrderUserEmail(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("""
            Update existing order by id in DB
            """)
    void update_OrderWithIdExists_ReturnsUpdatedOrderDto() {
        Long orderId = 1L;
        UpdateOrderRequestDto requestDto = new UpdateOrderRequestDto(Status.SENT);
        Order updatedOrder = order;
        updatedOrder.setStatus(requestDto.status());
        OrderDto expected = new OrderDto(
                orderDto.id(),
                orderDto.userId(),
                orderDto.orderDate(),
                orderDto.total(),
                requestDto.status(),
                orderDto.orderItems()
        );
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderMapper).toOrderEntity(requestDto, order);
        when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);
        when(orderMapper.toOrderDto(updatedOrder)).thenReturn(expected);

        OrderDto actual = orderService.update(orderId, requestDto);

        assertEquals(expected, actual);
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(updatedOrder);
    }

    @Test
    @DisplayName("""
            Update non-existing order by id in DB
            """)
    void update_OrderWithIdNotExists_ThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.update(1L, new UpdateOrderRequestDto(Status.SENT)));
        verify(orderRepository, times(1)).findById(anyLong());
    }
}
