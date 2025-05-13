package com.epam.rd.autocode.assessment.appliances.controller.client;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.OrderAccessDeniedException;
import com.epam.rd.autocode.assessment.appliances.exceptions.rare.NotFoundWhileUpdatingException;
import com.epam.rd.autocode.assessment.appliances.model.dto.order.OrderCreateDTO;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//Can be started from /cart and /orders

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrdersController {
    private final OrderService orderService;

    // client want to see cart
    @GetMapping("/cart")
    public String showCartPage() {
        return "order/cart";
    }

    // client makes fetch to createOrder
    @PostMapping("/cart")
    @ResponseBody
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderCreateDTO orderDTO, BindingResult bindingResult, Authentication authentication) {

        System.out.println("===========================");
        orderDTO.getOrderRowSet().forEach(r-> System.out.println(r.toString()));
        System.out.println(orderDTO.getCalculatedOnFrontTotal());
        System.out.println("===========================");

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        } else {
            if (authentication != null && authentication.isAuthenticated()) {
                Long clientID = Long.parseLong(authentication.getPrincipal().toString());
                orderService.create(orderDTO, clientID);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User is not logged in", HttpStatus.UNAUTHORIZED);
            }
        }
    }

    // client wants to cancel his order and row along with them
    @GetMapping("/orders/cancel/{orderId}")
    public String cancelByClient(@PathVariable Long orderId, Authentication authentication) {
        Long clientID = Long.parseLong(authentication.getPrincipal().toString());
        orderService.cancelOrderByClient(orderId, clientID);
        return "redirect:/cabinet";
    }

    @ExceptionHandler({NotFoundWhileUpdatingException.class, OrderAccessDeniedException.class})
    public String handleOrderCancelError(){
        return "error/404";
    }

}

