package com.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequestDto(
        @NotNull @Size(max = 255)
        String shippingAddress
) {
}
