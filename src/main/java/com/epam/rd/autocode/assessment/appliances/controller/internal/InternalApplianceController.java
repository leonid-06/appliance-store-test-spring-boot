package com.epam.rd.autocode.assessment.appliances.controller.internal;

import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceEditDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.manufacturer.ManufacturerDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Category;
import com.epam.rd.autocode.assessment.appliances.model.enums.PowerType;
import com.epam.rd.autocode.assessment.appliances.model.mappers.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequestMapping("/internal/appliances")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;
    private final ApplianceMapper mapper = ApplianceMapper.INSTANCE;

//    private static final String APPLIANCES_LIST_ATTR = "appliancesList";
//    private static final String APPLIANCES_FOR_EMPLOYEE_PAGE = "appliance/appliances-for-employee";
//    private static final String REDIRECT_TO_CABINET = "redirect:/cabinet";
//    private static final String CLIENT_RESPONSE_ATTR = "clientResponse";
//    private static final String CLIENT_EDIT_ATTR = "clientEdit";
//    private static final String ORDERS_LIST_ATTR = "ordersList";
//    private static final String EDIT_PAGE = "appliances";

//    without paging
//    @GetMapping
//    public String getAllAppliances(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            Model model) {
//
//        List<ApplianceResponseDTO> all = applianceService.findAll(page, size);
//        model.addAttribute(APPLIANCES, all);
//        return APPLIANCES_FOR_EMPLOYEE_PAGE;
//    }

    @GetMapping
    public String getAllAppliances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<ApplianceResponseDTO> appliancesPage = applianceService.findAllWithPagination(page, size);
        model.addAttribute(APPLIANCES, appliancesPage.getContent());
        model.addAttribute(CURRENT_PAGE, page);
        model.addAttribute(CURRENT_SIZE, size);
        model.addAttribute(TOTAL_ITEMS, appliancesPage.getTotalElements());
        model.addAttribute(TOTAL_PAGES, appliancesPage.getTotalPages());

        return APPLIANCES_FOR_EMPLOYEE_PAGE;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<ManufacturerDTO> manufacturers = manufacturerService.findAllActive(0, 100).getContent();
        model.addAttribute(CREATE_DTO, new ApplianceCreateDTO());
        model.addAttribute(MANUFACTURERS, manufacturers);
        model.addAttribute(CATEGORIES, Category.values());
        model.addAttribute(POWER_TYPES, PowerType.values());
        return APPLIANCE_NEW_PAGE;
    }

    @PostMapping
    public String createAppliance(@Valid @ModelAttribute(CREATE_DTO) ApplianceCreateDTO dto,
                              BindingResult bindingResult, Model model) {

        List<ManufacturerDTO> manufacturers = manufacturerService.findAllActive(0, 100).getContent();
        model.addAttribute(MANUFACTURERS, manufacturers);
        model.addAttribute(CATEGORIES, Category.values());
        model.addAttribute(POWER_TYPES, PowerType.values());

        if (bindingResult.hasErrors()) {
            return APPLIANCE_NEW_PAGE;
        }
        try {
            applianceService.create(dto);
            // todo add success pop-up
            return REDIRECT_TO_APPLIANCES_FOR_EMPLOYEE;
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("manufacturerId", "manufacturer.not.exist");
            return APPLIANCE_NEW_PAGE;
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long applianceID, Model model) {

        ApplianceResponseDTO appliance = applianceService.findById(applianceID);
        ApplianceEditDTO dto = mapper.toEditDTO(appliance);

        model.addAttribute(EDIT_DTO, dto);
        model.addAttribute(RESPONSE_DTO, appliance);
        model.addAttribute(CATEGORIES, Category.values());
        model.addAttribute(POWER_TYPES, PowerType.values());
        return APPLIANCE_EDIT_PAGE;
    }

    @PutMapping("/{id}/edit")
    public String updateAppliance(@Valid @ModelAttribute(EDIT_DTO) ApplianceEditDTO dto,
                                  BindingResult bindingResult,
                                  @PathVariable("id") Long applianceId, Model model) {

        if (bindingResult.hasErrors()) {
            ApplianceResponseDTO fullAppliance = applianceService.findById(applianceId);
            model.addAttribute(RESPONSE_DTO, fullAppliance);
            model.addAttribute(POWER_TYPES, PowerType.values());
            return APPLIANCE_EDIT_PAGE;
        }
        applianceService.update(applianceId, dto);
        return REDIRECT_TO_APPLIANCES_FOR_EMPLOYEE;
    }
}

