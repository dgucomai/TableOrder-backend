package dgucomai.tableorder.controller;

import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PatchMapping("/orders/{orderId}/approve")
    public ResponseEntity<?> approveOrder(@PathVariable Long orderId) {
        orderService.approveOrder(orderId);
        return getResponse(orderId, "ORDER_APPROVED", "주문 승인 완료", "COOKING", 101, "관리자", null);
    }

    @DeleteMapping("/staff/orders/{orderId}")
    public ResponseEntity<?> rejectOrder(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);
        return getResponse(orderId, "ORDER_REJECTED", "주문 반려 완료", "REJECTED", 2, "김직원", null);
    }

    @PatchMapping("/staff/orders/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        orderService.updateOrderStatus(orderId, newStatus);

        return getResponse(orderId, "ORDER_STATUS_CHANGED", "주문 상태 변경 완료", newStatus.toUpperCase(), 2, "김직원", LocalDateTime.now().toString());
    }

    private ResponseEntity<?> getResponse(Long orderId, String code, String msg, String oStatus, int staffId, String staffName, String completedAt) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("code", code);
        response.put("message", msg);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderId", orderId);
        data.put("orderStatus", oStatus);
        if (completedAt != null) data.put("completedAt", completedAt);
        data.put("changedByStaffId", staffId);
        data.put("changedByStaffName", staffName);

        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}