package com.epam.rd.autocode.assessment.appliances.model.dto.appliance;

import com.epam.rd.autocode.assessment.appliances.model.dto.manufacturer.ManufacturerDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.Category;
import com.epam.rd.autocode.assessment.appliances.model.enums.PowerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplianceResponseDTO {
    private Long id;
    private String name;
    private Category category;
    private String model;
    private ManufacturerDTO manufacturer;
    private PowerType powerType;
    private String characteristic;
    private String description;
    private Integer power;
    private BigDecimal price;

    private BigDecimal discount;
    private BigDecimal priceWithDiscount;
    private Long salesCount;
}

