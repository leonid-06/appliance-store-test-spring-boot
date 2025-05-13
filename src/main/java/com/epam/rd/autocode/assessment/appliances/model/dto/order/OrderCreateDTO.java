package com.epam.rd.autocode.assessment.appliances.model.dto.order;

import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class OrderCreateDTO {

    /**
     * It tells Validator to go through each object in the
     * collection and apply its own validation annotations to it.
     */
    @Valid
    private List<OrderRowCreateDTO> orderRowSet;

    // todo add constraint
    private BigDecimal calculatedOnFrontTotal;


    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "orderRowSet=" + orderRowSet +
                ", calculatedOnFrontTotal=" + calculatedOnFrontTotal +
                '}';
    }
}
