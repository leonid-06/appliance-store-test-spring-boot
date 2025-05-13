package com.epam.rd.autocode.assessment.appliances.model.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRowCreateDTO {

    @NotNull(message = "{validation.appliance-id.required}")
    private Long applianceId;
    @NotNull(message = "{validation.appliance.number.required}")
    private Long number;
}

