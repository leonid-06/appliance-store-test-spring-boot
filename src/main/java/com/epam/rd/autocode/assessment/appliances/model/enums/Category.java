package com.epam.rd.autocode.assessment.appliances.model.enums;

public enum Category {
    BIG, SMALL;
    public static boolean isValid(String value) {
        try {
            Category.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
