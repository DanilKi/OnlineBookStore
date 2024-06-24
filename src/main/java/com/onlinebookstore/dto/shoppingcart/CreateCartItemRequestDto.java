package com.onlinebookstore.dto.shoppingcart;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @Positive
    private Long bookId;
    @NotNull @Positive @Digits(integer = 2, fraction = 0)
    private Integer quantity;
}
