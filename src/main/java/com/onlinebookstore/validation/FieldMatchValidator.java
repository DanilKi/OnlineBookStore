package com.onlinebookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstField();
        secondFieldName = constraintAnnotation.secondField();
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext constraintValidatorContext) {
        try {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
            Object firstFieldValue = beanWrapper.getPropertyValue(firstFieldName);
            Object secondFieldValue = beanWrapper.getPropertyValue(secondFieldName);
            return firstFieldValue == null && secondFieldValue == null
                    || firstFieldValue != null && firstFieldValue.equals(secondFieldValue);
        } catch (Exception e) {
            throw new RuntimeException("Can't perform field match validation for '"
                    + firstFieldName + "' and '" + secondFieldName + "'", e);
        }
    }
}
