package com.epam.rd.autocode.assessment.appliances.controller.internal;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByNameException;
import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByPhoneException;
import com.epam.rd.autocode.assessment.appliances.model.dto.manufacturer.ManufacturerDTO;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequestMapping("/internal/manufacturers")
@RequiredArgsConstructor
public class InternalManufacturerController {

    private final ManufacturerService manufacturerService;
    private final MessageSource messageSource;

    @GetMapping
    public String getActiveManufacturers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<ManufacturerDTO> manufacturerPage = manufacturerService.findAllActive(page, size);
        model.addAttribute(MANUFACTURERS, manufacturerPage.getContent());
        model.addAttribute(CURRENT_PAGE, page);
        model.addAttribute(CURRENT_SIZE, size);
        model.addAttribute(TOTAL_ITEMS, manufacturerPage.getTotalElements());
        model.addAttribute(TOTAL_PAGES, manufacturerPage.getTotalPages());
        return MANUFACTURER_PAGE;
    }

    @GetMapping("/deleted")
    public String getDeletedAManufacturers(
            Model model) {
        List<ManufacturerDTO> deletedManufacturers = manufacturerService.findAllDeleted();
        model.addAttribute(MANUFACTURERS, deletedManufacturers);
        return MANUFACTURER_DELETED_PAGE;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute(CREATE_DTO, new ManufacturerDTO());
        return MANUFACTURER_NEW_PAGE;
    }

    @GetMapping("/{id}")
    public String getManufacturerById(@PathVariable Long id, Model model) {
        ManufacturerDTO dto = manufacturerService.findById(id);
        model.addAttribute(RESPONSE_DTO, dto);
        return MANUFACTURER_SHOW;
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute(CREATE_DTO) ManufacturerDTO createDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (bindingResult.hasErrors()) {
            return MANUFACTURER_NEW_PAGE;
        }

        try {
            manufacturerService.create(createDto);
            redirectAttributes.addFlashAttribute(
                    "success", messageSource.getMessage("banner.manufacturer-successfully-added", null, locale));
            return REDIRECT_TO_MANUFACTURERS;
        } catch (EntityExistsByNameException e) {
            bindingResult.rejectValue("name", "validation.name.already-exist");
            return MANUFACTURER_NEW_PAGE;
        } catch (EntityExistsByPhoneException e){
            bindingResult.rejectValue("phoneNumber", "validation.phone.already-exist");
            return MANUFACTURER_NEW_PAGE;
        }
    }


    @PutMapping("/{id}/edit")
    public String update(
            @PathVariable("id") Long manufacturerId,
            @Valid @ModelAttribute(EDIT_DTO) ManufacturerDTO manufacturerDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (bindingResult.hasErrors()) {
            return MANUFACTURER_SHOW;
        }

        try {
            manufacturerService.update(manufacturerId, manufacturerDto);
            redirectAttributes.addFlashAttribute(
                    "success", messageSource.getMessage("banner.manufacturer-successfully-updated", null, locale));
            return REDIRECT_TO_MANUFACTURERS;
        } catch (EntityExistsByNameException e) {
            bindingResult.rejectValue("name", "validation.name.already-exist");
            return MANUFACTURER_SHOW;
        } catch (EntityExistsByPhoneException e){
            bindingResult.rejectValue("phoneNumber", "validation.phone.already-exist");
            return MANUFACTURER_SHOW;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteManufacturer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        manufacturerService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Manufacturer successfully deleted");
        return REDIRECT_TO_MANUFACTURERS;
    }
}
