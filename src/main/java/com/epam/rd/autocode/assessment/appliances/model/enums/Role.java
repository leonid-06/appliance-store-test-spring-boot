package com.epam.rd.autocode.assessment.appliances.model.enums;

public enum Role {
    CLIENT,
    MANAGER,
    ADMIN;

    public static boolean isClient(String role) {
        return CLIENT.toString().equals(role);
    }

    public static boolean isEmployee(String role) {
        return MANAGER.toString().equals(role) || ADMIN.toString().equals(role);
    }
}
