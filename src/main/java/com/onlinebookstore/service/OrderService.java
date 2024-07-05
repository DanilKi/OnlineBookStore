package com.onlinebookstore.service;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.UpdateOrderRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto save(CreateOrderRequestDto requestDto, String userName);

    List<OrderDto> findAllByUser(Pageable pageable, String userName);

    List<OrderItemDto> findOrderItemsByOrderId(Long orderId, String userName);

    OrderItemDto findOrderItemById(Long orderId, Long orderItemId, String userName);

    OrderDto update(Long orderId, UpdateOrderRequestDto requestDto);
}
