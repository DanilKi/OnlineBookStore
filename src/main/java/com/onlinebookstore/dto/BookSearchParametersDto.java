package com.onlinebookstore.dto;

import com.onlinebookstore.validation.PriceRange;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

@PriceRange
public record BookSearchParametersDto(
        String[] titles,
        String[] authors,
        String isbn,
        @Digits(integer = 4, fraction = 2) @Positive
        String priceFrom,
        @Digits(integer = 4, fraction = 2) @Positive
        String priceTo
) {
}
