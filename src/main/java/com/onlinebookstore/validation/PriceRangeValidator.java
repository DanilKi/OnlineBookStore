package com.onlinebookstore.validation;

import com.onlinebookstore.dto.BookSearchParametersDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class PriceRangeValidator implements ConstraintValidator<PriceRange,
                                            BookSearchParametersDto> {

    @Override
    public void initialize(PriceRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookSearchParametersDto bookSearchParametersDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (bookSearchParametersDto.priceFrom() == null
                || bookSearchParametersDto.priceTo() == null) {
            return true;
        }
        return new BigDecimal(bookSearchParametersDto.priceFrom())
                .compareTo(new BigDecimal(bookSearchParametersDto.priceTo())) <= 0;
    }
}
