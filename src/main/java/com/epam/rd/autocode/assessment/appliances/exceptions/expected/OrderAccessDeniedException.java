package com.epam.rd.autocode.assessment.appliances.exceptions.expected;

public class OrderAccessDeniedException extends RuntimeException {
    public OrderAccessDeniedException(String message) {
        super(message);
    }
}
