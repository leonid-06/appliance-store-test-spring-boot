package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.dto.LoginUserDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Role;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndexController {

    private final ApplianceService applianceService;
    private static final String INDEX_PAGE = "index";

    private static final String DISCOUNT_APPLIANCES_ATTR = "discountedAppliances";
    private static final String POPULAR_APPLIANCES_ATTR = "mostPopularAppliances";

    @GetMapping("/")
    public String index(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String role = extractRole(auth);
            model.addAttribute(ROLE_ATTR, role);

            if (Role.isEmployee(role)) {
                model.addAttribute(ROLE_ATTR, role);
                return INDEX_PAGE;
            }

            if (Role.isClient(role)) {
                addApplianceAttributes(model);
                return INDEX_PAGE;
            }
        } else {
            model.addAttribute(CREATE_DTO, new ClientCreateDTO());
            model.addAttribute(LOGIN_DTO, new LoginUserDTO());
        }

        addApplianceAttributes(model);
        return INDEX_PAGE;
    }

    private void addApplianceAttributes(Model model) {
        List<ApplianceResponseDTO> discountedAppliances = applianceService.findTop5ByDiscountGreaterThan(0.25);
        List<ApplianceResponseDTO> mostPopularAppliances = applianceService.findTop5ByOrderBySalesCountDesc();

        model.addAttribute(DISCOUNT_APPLIANCES_ATTR, discountedAppliances);
        model.addAttribute(POPULAR_APPLIANCES_ATTR, mostPopularAppliances);
    }

    private String extractRole(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return role.substring(5);
    }
}
