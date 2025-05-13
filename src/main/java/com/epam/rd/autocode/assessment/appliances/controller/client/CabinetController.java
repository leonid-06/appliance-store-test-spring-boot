package com.epam.rd.autocode.assessment.appliances.controller.client;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByEmailException;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
@RequestMapping("/cabinet")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CabinetController {

    private final ClientService clientService;
    private final OrderService orderService;

    @GetMapping
    public String getClientCabinet(@RequestParam(name = "isCreated", required = false) boolean isCreated,
                                   Model model, Authentication authentication) {

        Long clientID = Long.parseLong(authentication.getPrincipal().toString());
        ClientResponseDTO clientDTO = clientService.findById(clientID);
        List<OrderResponseDTO> orders = orderService.findByClientId(clientID);

        model.addAttribute(FLASH_ATTR_ORDER_CREATED, isCreated);
        model.addAttribute(RESPONSE_DTO, clientDTO);
        model.addAttribute(ORDERS, orders);
        return CLIENT_CABINET_PAGE;
    }

    @GetMapping("/edit")
    public String getPageForEditClient(Model model, Authentication authentication) {
        Long clientID = Long.parseLong(authentication.getPrincipal().toString());
        ClientEditDTO toEditDTO = clientService.findByIdToEdit(clientID);
        model.addAttribute(EDIT_DTO, toEditDTO);
        return CLIENT_EDIT_PAGE;
    }

    @PutMapping
    public String processEditClient(@Valid @ModelAttribute(EDIT_DTO) ClientEditDTO client,
                             BindingResult bindingResult,
                             Authentication authentication) {

        if(bindingResult.hasErrors()) {
            return CLIENT_EDIT_PAGE;
        }
        long clientID = Long.parseLong(authentication.getPrincipal().toString());

        try {
            clientService.update(clientID, client);
        } catch (EntityExistsByEmailException e) {
            bindingResult.rejectValue("email", "validation.email.already-exist");
            return CLIENT_EDIT_PAGE;
        }

        return REDIRECT_TO_CABINET;
    }

//    @ExceptionHandler(NotFoundWhileUpdatingException.class)
//    public String handleEmailAlreadyInUse() {
//        return "error/404";
//    }

}
