package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    @PatchMapping("/{orderId}/approve")
    public Order approveOrder(@PathVariable Long orderId) {
        return orderService.approveOrder(orderId);
    }

    @PatchMapping("/{orderId}/reject")
    public Order rejectOrder(@PathVariable Long orderId) {
        return orderService.rejectOrder(orderId);
    }
}