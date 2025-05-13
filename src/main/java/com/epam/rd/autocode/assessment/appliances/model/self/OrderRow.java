package com.epam.rd.autocode.assessment.appliances.model.self;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class OrderRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Appliance appliance;

    private Long number;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public static OrderRow of(Appliance appliance, Long number) {
        OrderRow orderRow = new OrderRow();
        orderRow.appliance = appliance;
        orderRow.number = number;
        return orderRow;
    }

    @Override
    public String toString() {
        return "OrderRow{" +
                "id=" + id +
                ", appliance=" + appliance.getName() +
                ", number=" + number +
                ", order=" + order.getId() +
                '}';
    }
}
