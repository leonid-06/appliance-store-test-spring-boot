package com.epam.rd.autocode.assessment.appliances.controller.client;

import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Category;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplianceController {

    private final ApplianceService service;

    // client want to see all appliances
    @GetMapping("/appliances")
    public String getAllAppliances(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) boolean discounted,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "9") int size,
            @RequestParam(required = false) String success,
            Model model) {

        if (category != null && !category.isBlank() && !Category.isValid(category) ) {
            return "error/404";
        }

        System.out.println("=========================");
        System.out.println(category);
        System.out.println(sort);
        System.out.println(discounted);
        System.out.println("=========================");

        // todo remove
        if (success != null) {
            model.addAttribute("success", success);
        }

        Page<ApplianceResponseDTO> appliancesPage = service.getAppliances(
                category, discounted, sort, PageRequest.of(page, size));

        model.addAttribute(APPLIANCES, appliancesPage.getContent());
        model.addAttribute(CATEGORIES, Category.values());
        model.addAttribute(CURRENT_PAGE, page);
        model.addAttribute(TOTAL_PAGES, appliancesPage.getTotalPages());
        model.addAttribute(CURRENT_SIZE, size);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("discounted", discounted);

        return APPLIANCES_FOR_CLIENT_PAGE;
    }
}

