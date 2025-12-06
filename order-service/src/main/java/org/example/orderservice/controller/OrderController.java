package org.example.orderservice.controller;

import org.example.orderservice.dto.OrderDTO.OrderCreateDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderCreateDTO dto) {
        return orderService.createOrderFromDTO(dto);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
