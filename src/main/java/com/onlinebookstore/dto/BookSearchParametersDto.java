package com.onlinebookstore.dto;

public record BookSearchParametersDto(
        String[] titles,
        String[] authors,
        String isbn,
        String priceFrom,
        String priceTo) {
}
