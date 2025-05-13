package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.OrderAccessDeniedException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileDeletingException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.OrderCreateException;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderResponseDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderRowCreateDTO;
import com.epam.rd.autocode.assessment.appliances.model.enums.OrderStatus;
import com.epam.rd.autocode.assessment.appliances.model.mappers.OrderMapper;
import com.epam.rd.autocode.assessment.appliances.model.mappers.OrderRowMapper;
import com.epam.rd.autocode.assessment.appliances.model.self.*;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final ClientService clientService;
    private final EmployeeService employeeService;

    private final OrderRepository repo;

    @Deprecated
    private final ClientRepository clientRepo;
    @Deprecated
    private final EmployeeRepository employeeRepo;
    @Deprecated
    private final ApplianceRepository applianceRepo;
    @Deprecated
    private final OrderRowRepository orderRowRepo;

    private final OrderMapper mapper = OrderMapper.INSTANCE;

//    @Override
//    @Transactional
//    public OrderResponseDTO create(OrderCreateDTO dto, Long clientId) {
//        log.info("Attempt to create order by client with id {}", clientId);
//
//        // Validate input
//        if (dto == null || dto.getOrderRowSet() == null || dto.getOrderRowSet().isEmpty()) {
//            throw new OrderCreateException("Order creation data is invalid or empty");
//        }
//
//        // Retrieve client
//        Client client = clientRepo.findById(clientId)
//                .orElseThrow(() -> new OrderCreateException(
//                        String.format("Client with id %d not found while creating order", clientId)));
//
//        // Create order
//        Order order = Order.of(client);
//        BigDecimal calculatedTotal = BigDecimal.ZERO;
//        Set<OrderRow> orderRows = new HashSet<>();
//
//        // Prefetch appliances to reduce database calls
//        Set<Long> applianceIds = dto.getOrderRowSet().stream()
//                .map(OrderRowCreateDTO::getApplianceId)
//                .collect(Collectors.toSet());
//
//        Map<Long, Appliance> appliances = applianceRepo.findAllById(applianceIds).stream()
//                .collect(Collectors.toMap(Appliance::getId, Function.identity()));
//
//        // Process each order row
//        for (OrderRowCreateDTO row : dto.getOrderRowSet()) {
//            if (row.getNumber() <= 0) {
//                throw new OrderCreateException("Invalid quantity for appliance with id " + row.getApplianceId());
//            }
//
//            Appliance appliance = appliances.get(row.getApplianceId());
//            if (appliance == null) {
//                throw new OrderCreateException(
//                        String.format("Appliance with id %d not found while creating order", row.getApplianceId()));
//            }
//
//            OrderRow orderRow = OrderRow.of(appliance, row.getNumber());
//            orderRow.setOrder(order); // Ensure bidirectional relationship
//            orderRows.add(orderRow);
//
//            calculatedTotal = calculatedTotal.add(
//                    appliance.getPrice().multiply(BigDecimal.valueOf(row.getNumber())));
//        }
//
//        // Validate total price
//        log.debug("Calculated total: {}, Frontend total: {}", calculatedTotal, dto.getCalculatedOnFrontTotal());
//        if (calculatedTotal.compareTo(dto.getCalculatedOnFrontTotal()) != 0) {
//            throw new OrderCreateException("Prices have changed. Please refresh and try again");
//        }
//
//        // Set order rows and save
//        order.setOrderRowSet(orderRows);
//        Order savedOrder = repo.save(order);
//
//        log.info("Order created successfully with id {} by client with id {}", savedOrder.getId(), clientId);
//        return mapper.toResponseDTO(savedOrder);
//    }


    @Override
    @Transactional
    public OrderResponseDTO create(OrderCreateDTO dto, Long clientID) {
        log.info("Attempt to create order by client with id {}", clientID);

        // retrieve client
        Client client = clientRepo.findById(clientID).orElseThrow(
                () -> new OrderCreateException("No such client with id " + clientID + " while creating order")
        );

        // create order and order rows
        Order order = Order.of(client);
//        ArrayList<OrderRow> orderRows = new ArrayList<>();

        AtomicReference<BigDecimal> calculatedOnBackTotal = new AtomicReference<>(BigDecimal.ZERO);


        List<OrderRow> orderRows = dto.getOrderRowSet().stream()
                .map(rowDTO -> {

                    Appliance appliance = applianceRepo.findById(rowDTO.getApplianceId()).orElseThrow(
                            () -> new OrderCreateException("No such appliance with id " + rowDTO.getApplianceId() + " while creating order")
                    );

                    calculatedOnBackTotal.updateAndGet(current -> current.add(
                            appliance.getPrice().multiply(BigDecimal.valueOf(rowDTO.getNumber()))
                    ));

                    OrderRow orderRow = OrderRow.of(appliance, rowDTO.getNumber());
                    orderRow.setOrder(order); // back relative
                    return orderRow;
                }).toList();


        order.setOrderRowSet(orderRows);
        if (calculatedOnBackTotal.get().compareTo(dto.getCalculatedOnFrontTotal()) != 0){
            // todo say about price changed. Try again
            throw new OrderCreateException("price changed");
        } else {
            order.setPriceAtPurchase(calculatedOnBackTotal.get());
            Order saved = repo.save(order);
            log.info("Order created successfully by client with id {}", clientID);
            return mapper.toResponseDTO(saved);
        }
    }

    @Override
    public List<OrderResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Order> orders = repo.findAll(pageable).getContent();
        return orders.stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Attempt to delete order by id {}", id);

        Order order = repo.findById(id).orElseThrow(NotFoundWhileDeletingException::new);
        order.setStatus(OrderStatus.CANCELLED_BY_EMPLOYEE);
//        orderRowRepo.deleteAll(order.getOrderRowSet());
//        repo.deleteById(id);
        repo.save(order);
    }

    @Override
    public OrderResponseDTO approveOrder(Long orderID, Long employeeID) {
        log.info("Attempt to approve order with id {} by employee with id {}", orderID, employeeID);
        Order order = repo.findById(orderID).orElseThrow(NotFoundWhileUpdatingException::new);
        order.setStatus(OrderStatus.CONFIRMED);
        Employee employee = employeeRepo.findById(employeeID).orElseThrow(NotFoundWhileUpdatingException::new);
        order.setEmployee(employee);
        Order saved = repo.save(order);
        log.info("Order with id {} approved successfully by employee with id {}", orderID, employeeID);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public List<OrderResponseDTO> findByEmployeeId(Long employeeId) {
        return repo.findByEmployeeId(employeeId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrderByClient(Long orderId, Long clientID) {
        log.info("Attempting to cancel order [ID: {}] by client [ID: {}]", orderId, clientID);

        if(clientService.findEntityById(clientID).isEmpty()) {
            log.error("Client not found while cancelling order. Client ID: {}", clientID);
            throw new NotFoundWhileUpdatingException();
        }

        Order order = repo.findById(orderId).orElseThrow(
                () -> {
                    log.error("Order not found while cancelling. Order ID: {}", orderId);
                    return new NotFoundWhileUpdatingException();
                }
        );

        if (order.getClient() == null || !order.getClient().getId().equals(clientID)) {
            log.warn("Access denied: Order [ID: {}] doesn't belong to client [ID: {}]",
                    orderId, clientID);
            throw new OrderAccessDeniedException("Order doesn't belong to the client");
        }

        order.setStatus(OrderStatus.CANCELLED_BY_CLIENT);
        log.info("Order [ID: {}] successfully cancelled by client [ID: {}]", orderId, clientID);
    }

    @Override
    @Transactional
    public void cancelOrderByEmployee(Long orderId, Long employeeID) {
        log.info("Attempting to cancel order [ID: {}] by employee [ID: {}]", orderId, employeeID);

        if(employeeService.findEntityById(employeeID).isEmpty()) {
            log.error("Client not found while cancelling order. Employee: {}", employeeID);
            throw new NotFoundWhileUpdatingException();
        }

        Order order = repo.findById(orderId).orElseThrow(
                () -> {
                    log.error("Order  not found while cancelling. Order ID: {}", orderId);
                    return new NotFoundWhileUpdatingException();
                }
        );

        if (order.getEmployee() == null || !order.getEmployee().getId().equals(employeeID)) {
            log.warn("Access  denied: Order [ID: {}] doesn't belong to client [ID: {}]",
                    orderId, employeeID);
            throw new OrderAccessDeniedException("Order doesn't belong to the client");
        }

        order.setStatus(OrderStatus.CANCELLED_BY_EMPLOYEE);
        log.info("Order [ID: {}] successfully cancelled by employee [ID: {}]", orderId, employeeID);
    }

    @Override
    public List<OrderResponseDTO> findByClientId(Long id) {
        List<OrderStatus> statuses = List.of(
                OrderStatus.PENDING,
                OrderStatus.CANCELLED_BY_EMPLOYEE,
                OrderStatus.CONFIRMED
        );

        return repo.findByClientIdAndStatusIn(id, statuses)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}