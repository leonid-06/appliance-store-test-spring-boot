package com.epam.rd.autocode.assessment.appliances.model.dto.order;

import com.epam.rd.autocode.assessment.appliances.model.self.Client;
import com.epam.rd.autocode.assessment.appliances.model.self.Employee;
import com.epam.rd.autocode.assessment.appliances.model.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private Client client;
    private Employee employee;
    private List<OrderRowResponseDTO> orderRowSet;
    private OrderStatus status;

    private BigDecimal priceAtPurchase;

    public BigDecimal getTotal(){
        return priceAtPurchase;
    }

    public String toString() {
        return "OrdersDTO{" +
                "id=" + id +
                ", client=" + ((client==null) ? "n/a" : client.getName()) +
                ", employee=" + ((employee==null) ? "n/a" : employee.getName()) +
                ", orderRowSet=" + orderRowSet +
                ", status=" + status +
                '}';
    }
}
