package com.epam.rd.autocode.assessment.appliances.controller;//package com.epam.rd.autocode.assessment.appliances.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ApplianceForEmployeeNotFoundException.class)
//    public String handleApplianceError(ApplianceForEmployeeNotFoundException ex, Model model) {
//        model.addAttribute("error_message", ex.getMessage());
//        return "error/500";
//    }
//
//    @ExceptionHandler(ClientNotFoundForClientException.class )
//    public String handleClientError() {
//        return "error/500";
//    }
//
//    @ExceptionHandler(OrderException.class )
//    public String handleOrderError() {
//        return "error/500";
//    }
//
//    @ExceptionHandler(EmployeeException.class)
//    public String handleEmployeeError(EmployeeException ex, Model model) {
//        model.addAttribute("error_message", ex.getMessage());
//        return "error/500";
//    }
//
//}
