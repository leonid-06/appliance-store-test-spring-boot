package com.epam.rd.autocode.assessment.appliances.model.dto.order;

import com.epam.rd.autocode.assessment.appliances.model.dto.appliance.ApplianceResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.self.Appliance;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderRowResponseDTO {
    private Long id;
    private ApplianceResponseDTO appliance;
    private Long number;
    private BigDecimal amount;

    @Override
    public String toString() {
        return "OrderRowDTO{" +
                "id=" + id +
                ", appliance=" + appliance.getName() +
                ", number=" + number +
                ", amount=" + amount +
                '}';
    }
}
