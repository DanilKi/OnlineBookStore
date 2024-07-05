package com.onlinebookstore.dto.order;

import com.onlinebookstore.model.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long userId,
        LocalDateTime orderDate,
        BigDecimal total,
        Status status,
        List<OrderItemDto> orderItems
) {
}
