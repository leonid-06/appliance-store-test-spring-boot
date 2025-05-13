package com.epam.rd.autocode.assessment.appliances.model.self;

import com.epam.rd.autocode.assessment.appliances.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Client client;
    @ManyToOne
    private Employee employee;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderRow> orderRowSet;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal priceAtPurchase;

    public static Order of(final Client client){
        Order order = new Order();
        order.setClient(client);
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", client=" + client.getName() +
                ", employee=" + employee.getName() +
                ", orderRowCount=" + orderRowSet.size() +
                ", status=" + status +
                '}';
    }
}


