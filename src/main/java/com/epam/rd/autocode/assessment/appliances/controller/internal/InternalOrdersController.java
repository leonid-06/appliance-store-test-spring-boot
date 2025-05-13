package com.epam.rd.autocode.assessment.appliances.controller.internal;

import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/internal/orders")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalOrdersController {
    private final OrderService orderService;

    @GetMapping
    public String orders(Model model) {
        List<OrderResponseDTO> all = orderService.findAll(0, 100);
        model.addAttribute("orders", all);
        return "order/list";
    }

    @GetMapping("/{id}/approved")
    public String approve(@PathVariable Long id, Authentication authentication) {
        Long employeeID = Long.parseLong(authentication.getPrincipal().toString());

        orderService.approveOrder(id, employeeID);
        return "redirect:/internal/orders";
    }

    @GetMapping("/{id}/cancel")
    public String cancelByEmployee(@PathVariable Long id, Authentication authentication) {
        Long employeeID = Long.parseLong(authentication.getPrincipal().toString());

        orderService.cancelOrderByEmployee(id, employeeID);
        return "redirect:/internal/orders";
    }


    @GetMapping("/{orderId}/delete")
    public String delete(@PathVariable Long orderId) {
        orderService.deleteById(orderId);
        return "redirect:/internal/orders";
    }
}
