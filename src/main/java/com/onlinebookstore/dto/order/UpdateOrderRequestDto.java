package com.onlinebookstore.dto.order;

import com.onlinebookstore.model.Status;
import com.onlinebookstore.validation.StatusSubset;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequestDto(
        @NotNull
        @StatusSubset(anyOf = {Status.PENDING, Status.SENT, Status.DELIVERED, Status.COMPLETED})
        Status status
) {
}
