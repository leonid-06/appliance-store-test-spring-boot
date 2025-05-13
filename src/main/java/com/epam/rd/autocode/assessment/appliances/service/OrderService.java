package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO create(OrderCreateDTO createDTO, Long clientID);
    List<OrderResponseDTO> findByClientId(Long id);
    List<OrderResponseDTO> findByEmployeeId(Long employeeID);
    List<OrderResponseDTO> findAll(int page, int size);

    void deleteById(Long id);
    OrderResponseDTO approveOrder(Long orderID, Long employeeID);

    void cancelOrderByClient(Long orderId, Long clientID);
    void cancelOrderByEmployee(Long orderId, Long clientID);
}
