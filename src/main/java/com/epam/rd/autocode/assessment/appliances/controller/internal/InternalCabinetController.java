package com.epam.rd.autocode.assessment.appliances.controller.internal;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByEmailException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.employee.EmployeeResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequestMapping("/internal/cabinet")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalCabinetController {

    private final EmployeeService employeeService;
    private final OrderService orderService;


    @GetMapping
    public String getEmployeeCabinet(Model model, Authentication authentication) {
        Long employeeID = Long.parseLong(authentication.getPrincipal().toString());
        EmployeeResponseDTO employee = employeeService.findById(employeeID);
        List<OrderResponseDTO> orders = orderService.findByEmployeeId(employeeID);

        model.addAttribute(RESPONSE_DTO, employee);
        model.addAttribute(ORDERS, orders);
        return EMPLOYEE_CABINET_PAGE;
    }

    @GetMapping("/edit")
    public String editEmployee(Model model, Authentication authentication) {
        Long employeeID = Long.parseLong(authentication.getPrincipal().toString());

        EmployeeEditDTO dto = employeeService.findByIdToEdit(employeeID);
        model.addAttribute(EDIT_DTO, dto);

        return EMPLOYEE_EDIT_PAGE;
    }

    @PutMapping
    public String processEdit(@Valid @ModelAttribute(EDIT_DTO) EmployeeEditDTO employee,
                              BindingResult result,
                              Authentication authentication) {

        if (result.hasErrors()) {
            return EMPLOYEE_EDIT_HIMSELF_PAGE;
        }

        long employeeID = Long.parseLong(authentication.getPrincipal().toString());

        try {
            employeeService.update(employee, employeeID);
            return REDIRECT_TO_CABINET;
        } catch (EntityExistsByEmailException e) {
            result.rejectValue("email", "validation.email.already-exist");
            return EMPLOYEE_EDIT_HIMSELF_PAGE;
        }
    }

    @ExceptionHandler(NotFoundWhileUpdatingException.class)
    public String handleError() {
        // todo message for employee in Model
        return "error/404";
    }

}
