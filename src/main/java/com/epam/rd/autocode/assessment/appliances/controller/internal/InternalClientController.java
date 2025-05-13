package com.epam.rd.autocode.assessment.appliances.controller.internal;

import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/internal/clients")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalClientController {

    private final ClientService service;
    private final OrderService orderService;

    @GetMapping
    public String all(Model model) {
        List<ClientResponseDTO> all = service.findAll(0, 100);
        model.addAttribute("clients", all);
        return "client/clients";
    }

    @GetMapping("/{id}")
    public String byId(@PathVariable Long id, Model model) {
        ClientResponseDTO client = service.findById(id);
        List<OrderResponseDTO> orders = orderService.findByEmployeeId(id);

        model.addAttribute("orders", orders);
        model.addAttribute("client", client);
        return "client/about";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/internal/clients";
    }
}
