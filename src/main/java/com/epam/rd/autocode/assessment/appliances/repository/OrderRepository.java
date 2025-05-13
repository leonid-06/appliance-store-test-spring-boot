package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.enums.OrderStatus;
import com.epam.rd.autocode.assessment.appliances.model.self.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long id);
    List<Order> findByEmployeeId(Long employeeId);
    List<Order> findByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses);
}
