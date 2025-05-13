package com.epam.rd.autocode.assessment.appliances.controller.internal;

import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.epam.rd.autocode.assessment.appliances.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequestMapping("/internal/employees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalEmployeeController {

    private final EmployeeService employeeService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String showAll(Model model) {
        List<EmployeeResponseDTO> all = employeeService.findAll(0, 100);
        model.addAttribute(EMPLOYEES, all);
        return EMPLOYEES_PAGE;
    }

    @GetMapping("/{id}")
    public String showById(Model model, @PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.findById(id);
        List<OrderResponseDTO> orders = orderService.findByEmployeeId(id);

        model.addAttribute(RESPONSE_DTO, employee);
        model.addAttribute(ORDERS, orders);
        model.addAttribute("forAdmin", true);
        return EMPLOYEE_CABINET_PAGE;
    }

    @GetMapping("/new")
    public String showNewEmployeeForm(Model model) {
        model.addAttribute(CREATE_DTO, new EmployeeCreateDTO());
        // todo hardcode
        model.addAttribute(DEPARTMENTS, List.of("Development", "QA", "Devops", "Sales"));
        return EMPLOYEE_NEW_PAGE;
    }

    @PostMapping
    public String createEmployee(@Valid @ModelAttribute(CREATE_DTO) EmployeeCreateDTO employee,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return EMPLOYEE_NEW_PAGE;
        }

        if (userService.existsByEmail(employee.getEmail())) {
            result.rejectValue("email", "validation.email.already-exist");
            return EMPLOYEE_NEW_PAGE;
        }
        employeeService.create(employee);

        return REDIRECT_TO_EMPLOYEES;
    }


    @GetMapping("/{id}/edit")
    public String showEditEmployeeForm(Model model, @PathVariable Long id) {
        EmployeeEditDTO employee = employeeService.findByIdToEdit(id);
        model.addAttribute(EDIT_DTO, employee);
        model.addAttribute("employeeId", id);
        model.addAttribute("roles", List.of(Role.ADMIN, Role.MANAGER));

        return EMPLOYEE_EDIT_BY_ADMIN_PAGE;
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        employeeService.deleteById(id);
        return REDIRECT_TO_EMPLOYEES;
    }

    @PutMapping("/{id}")
    public String processEdit(@Valid @ModelAttribute(EDIT_DTO) EmployeeEditDTO employee, BindingResult bindingResult,
                              @PathVariable Long id, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", List.of(Role.ADMIN, Role.MANAGER));
            return EMPLOYEE_EDIT_BY_ADMIN_PAGE;
        }

        try {
            employeeService.update(employee, id);
        } catch (EntityExistsException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return EMPLOYEE_EDIT_BY_ADMIN_PAGE;
        }

        return "redirect:/internal/employees/" + id;
    }
}