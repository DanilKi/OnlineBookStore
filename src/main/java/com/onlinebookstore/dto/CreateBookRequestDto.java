package com.onlinebookstore.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateBookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotNull @ISBN
    private String isbn;
    @NotNull @Positive @Digits(integer = 4, fraction = 2)
    private BigDecimal price;
    @Size(max = 255)
    private String description;
    @URL
    private String coverImage;
}
