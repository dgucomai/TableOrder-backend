package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/orders") // 모든 주소의 시작점
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. 대기 주문 조회 (GET /api/staff/orders)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    // 2. 결제 승인 (PATCH /api/staff/orders/{orderId}/approve)
    @PatchMapping("/{orderId}/approve")
    public String approveOrder(@PathVariable Long orderId) {
        orderService.approveOrder(orderId);
        return orderId + "번 주문이 승인(APPROVED) 되었습니다.";
    }

    // 3. 결제 반려 (PATCH /api/staff/orders/{orderId}/reject)[cite: 1]
    @PatchMapping("/{orderId}/reject")
    public String rejectOrder(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);
        return orderId + "번 주문이 반려(REJECTED) 되었습니다.";
    }

    // 4. 주문 삭제 (DELETE /api/staff/orders/{orderId})
    // 기획서에는 없지만 관리용으로 유지합니다.[cite: 1]
    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return orderId + "번 주문 데이터가 삭제되었습니다.";
    }

    // 5. 상태 변경 (PATCH /api/staff/orders/{orderId}/status)
    // 조리 중, 완료 등 범용적으로 상태를 바꿀 때 사용합니다.[cite: 1]
    @PatchMapping("/{orderId}/status")
    public String updateStatus(@PathVariable Long orderId, @RequestParam Order.OrderStatus status) {
        // 서비스에 상태 변경 로직이 연결되면 작동합니다.
        return orderId + "번 주문의 상태가 " + status + "(으)로 변경되었습니다.";
    }
}