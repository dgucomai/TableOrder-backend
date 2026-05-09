package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    @PatchMapping("/{orderId}/approve")
    public String approve(@PathVariable Long orderId) {
        orderService.approveOrder(orderId);
        return "Order " + orderId + " approved.";
    }

    @PatchMapping("/{orderId}/reject")
    public String reject(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);
        return "Order " + orderId + " rejected.";
    }
}