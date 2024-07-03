package com.onlinebookstore.validation;

import com.onlinebookstore.model.Status;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class StatusSubsetValidator implements ConstraintValidator<StatusSubset, Status> {
    private Status[] subset;

    @Override
    public void initialize(StatusSubset constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(Status status, ConstraintValidatorContext constraintValidatorContext) {
        return status == null || Arrays.asList(subset).contains(status);
    }
}
