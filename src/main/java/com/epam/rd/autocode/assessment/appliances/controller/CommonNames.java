package com.epam.rd.autocode.assessment.appliances.controller;

// Common attribute, page names and sorting options
public interface CommonNames {
    String CREATE_DTO = "createDTO";
    String EDIT_DTO = "editDTO";
    String RESPONSE_DTO = "responseDTO";
    String LOGIN_DTO = "loginDto";

    String MANUFACTURERS = "manufacturers";
    String EMPLOYEES = "employees";
    String APPLIANCES = "appliances";
    String CATEGORIES = "categories";
    String POWER_TYPES = "powerTypes";
    String DEPARTMENTS = "departments";
    String ORDERS = "orders";
    String ROLE_ATTR = "role";

    String INDEX_PAGE = "index";
    String REDIRECT_TO_HOME = "redirect:/";

    String APPLIANCES_FOR_EMPLOYEE_PAGE = "appliance/list-for-employee";
    String APPLIANCES_FOR_CLIENT_PAGE = "appliance/list-for-client";
    String APPLIANCE_EDIT_PAGE = "appliance/edit";
    String APPLIANCE_NEW_PAGE = "appliance/new";

    String MANUFACTURER_PAGE = "manufacturer/list";
    String MANUFACTURER_DELETED_PAGE = "manufacturer/deleted-list";
    String MANUFACTURER_EDIT_PAGE = "manufacturer/edit";
    String MANUFACTURER_NEW_PAGE = "manufacturer/new";
    String MANUFACTURER_SHOW = "manufacturer/show";


    String EMPLOYEES_PAGE = "employee/list";
    String EMPLOYEE_EDIT_PAGE = "employee/edit";
    String EMPLOYEE_NEW_PAGE = "employee/new";
    String EMPLOYEE_CABINET_PAGE = "employee/cabinet";
    String EMPLOYEE_EDIT_HIMSELF_PAGE = "employee/edit-by-employee";
    String EMPLOYEE_EDIT_BY_ADMIN_PAGE= "employee/edit-by-admin";



    String CLIENT_CABINET_PAGE = "client/cabinet";
    String CLIENT_EDIT_PAGE = "client/edit";


    String CURRENT_PAGE = "currentPage";
    String CURRENT_SIZE = "currentSize";
    String TOTAL_PAGES = "totalPages";
    String TOTAL_ITEMS = "totalItems";



    String REDIRECT_TO_APPLIANCES_FOR_EMPLOYEE = "redirect:/internal/appliances";
    String REDIRECT_TO_APPLIANCES_FOR_CLIENT = "redirect:/appliances";
    String REDIRECT_TO_MANUFACTURERS = "redirect:/internal/manufacturers";
    String REDIRECT_TO_EMPLOYEES = "redirect:/internal/employees";
    String REDIRECT_TO_CABINET = "redirect:/cabinet";



    String FLASH_ATTR_ORDER_CREATED = "isCreated";




    // sort options
    String EXPENSIVE = "expensive";
    String CHEAP = "cheap";
    String POWER = "power";

}

