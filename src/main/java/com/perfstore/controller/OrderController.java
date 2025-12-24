package com.perfstore.controller;

import com.perfstore.dto.OrderDto;
import com.perfstore.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto.Response> createOrder(@RequestBody OrderDto.CreateRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.createOrder(username, request));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto.Response>> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.getUserOrders(username));
    }
}
