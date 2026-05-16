package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Java 21 record: 응답 데이터를 위한 간결한 구조체 (성공 이미지 규격 맞춤)
    public record OrderStatusData(
            Long orderId,
            String orderStatus,
            String completedAt,
            int changedByStaffId,
            String changedByStaffName
    ) {}

    // 1. 주문 승인
    @PatchMapping("/orders/{orderId}/approve")
    public ResponseEntity<ApiResDto<OrderStatusData>> approveOrder(@PathVariable Long orderId) {
        orderService.approveOrder(orderId);

        var data = new OrderStatusData(orderId, "COOKING", null, 101, "관리자");
        return ResponseEntity.ok(new ApiResDto<>(true, data, "ORDER_APPROVED", "주문 승인 완료"));
    }

    // 2. 주문 반려 (삭제)
    @DeleteMapping("/staff/orders/{orderId}")
    public ResponseEntity<ApiResDto<OrderStatusData>> rejectOrder(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);

        var data = new OrderStatusData(orderId, "REJECTED", null, 2, "김직원");
        return ResponseEntity.ok(new ApiResDto<>(true, data, "ORDER_REJECTED", "주문 반려 완료"));
    }

    // 3. 상태 변경 (COMPLETED 등)
    @PatchMapping("/staff/orders/{orderId}/status")
    public ResponseEntity<ApiResDto<OrderStatusData>> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        String newStatus = request.get("status");
        orderService.updateOrderStatus(orderId, newStatus);

        var data = new OrderStatusData(
                orderId,
                newStatus.toUpperCase(),
                "COMPLETED".equalsIgnoreCase(newStatus) ? LocalDateTime.now().toString() : null,
                2,
                "김직원"
        );

        return ResponseEntity.ok(new ApiResDto<>(true, data, "ORDER_STATUS_CHANGED", "주문 상태 변경 완료"));
    }

    // --- 기존 기능 유지 ---

    @PostMapping("/staff-call")
    public ResponseEntity<ApiResDto<Void>> staffCall(@RequestParam Long tableId) {
        orderService.callStaff(tableId);
        return ResponseEntity.ok(new ApiResDto<>(true, null, "STAFF_CALL_CREATED"));
    }

    @PostMapping("/dealer-call")
    public ResponseEntity<ApiResDto<Void>> dealerCall(@RequestParam Long tableId) {
        orderService.callDealer(tableId);
        return ResponseEntity.ok(new ApiResDto<>(true, null, "DEALER_CALL_CREATED"));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResDto<OrderResDto>> createOrder(
            @RequestBody OrderCreateReqDto request) {
        OrderResDto response = orderService.createOrder(request);
        return new ResponseEntity<>(
                new ApiResDto<>(true, response, "PAYMENT_REQUEST_CREATED"), HttpStatus.CREATED);
    }
}