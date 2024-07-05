package com.onlinebookstore.validation;

import com.onlinebookstore.model.Status;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusSubsetValidator.class)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusSubset {
    Status[] anyOf();
    String message() default "Status must be any of {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
