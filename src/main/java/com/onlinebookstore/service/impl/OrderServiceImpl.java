package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.UpdateOrderRequestDto;
import com.onlinebookstore.exception.DataProcessingException;
import com.onlinebookstore.mapper.OrderItemMapper;
import com.onlinebookstore.mapper.OrderMapper;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import com.onlinebookstore.model.RoleName;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.Status;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.order.OrderItemRepository;
import com.onlinebookstore.repository.order.OrderRepository;
import com.onlinebookstore.service.OrderService;
import com.onlinebookstore.service.ShoppingCartService;
import com.onlinebookstore.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto save(CreateOrderRequestDto requestDto, String userName) {
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(userName);
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new DataProcessingException("Can't place an order. No items found in the "
                    + "shopping cart of the user: " + userName);
        }
        Order order = orderMapper.toOrderEntity(requestDto);
        if (order.getShippingAddress().isBlank()) {
            order.setShippingAddress(shoppingCart.getUser().getShippingAddress());
        }
        order.setUser(shoppingCart.getUser());
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(shoppingCart.getCartItems().stream()
                .map(orderItemMapper::toOrderItemEntity)
                .collect(Collectors.toSet()));
        order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));
        order.setTotal(order.getOrderItems().stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        orderRepository.save(order);
        shoppingCart.getCartItems().clear();
        return orderMapper.toOrderDto(order);
    }

    @Override
    public List<OrderDto> findAllByUser(Pageable pageable, String userName) {
        Page<Order> orders;
        if (hasSupervisorAccess(userService.getUserByEmail(userName))) {
            orders = orderRepository.findAll(pageable);
        } else {
            orders = orderRepository.findAllByUserEmail(userName, pageable);
        }
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> findOrderItemsByOrderId(Long orderId, String userName) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, userName).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + orderId
                        + " for user: " + userName)
        );
        return order.getOrderItems().stream()
                .map(orderItemMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto findOrderItemById(Long orderId, Long orderItemId, String userName) {
        return orderItemMapper.toOrderItemDto(
            orderItemRepository.findByIdAndOrderIdAndOrderUserEmail(orderItemId, orderId, userName)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Can't find order item by id: "
                                    + orderItemId + " with order id: " + orderId
                                    + " for user: " + userName)
                    )
        );
    }

    @Override
    public OrderDto update(Long orderId, UpdateOrderRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't update order by id: " + orderId)
        );
        orderMapper.toOrderEntity(requestDto, order);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    private boolean hasSupervisorAccess(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN
                        || role.getName() == RoleName.MANAGER);
    }
}
