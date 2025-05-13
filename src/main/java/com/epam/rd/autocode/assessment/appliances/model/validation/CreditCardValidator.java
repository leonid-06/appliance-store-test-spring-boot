package com.epam.rd.autocode.assessment.appliances.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CreditCardValidator implements ConstraintValidator<CreditCard, String> {

    @Value("${validation.card.pattern.regexp}")
    private String regex;

    private Pattern pattern;

    @Override
    public void initialize(CreditCard constraintAnnotation) {
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }
        return pattern.matcher(s).matches();
    }
}
