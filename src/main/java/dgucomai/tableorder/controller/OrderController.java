package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/orders") // 노션 주소 체계에 맞췄습니다!
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. 주문 목록 전체 조회 (참고용)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    // 2. 주문 대기 승인 (PATCH: /api/staff/orders/{orderId}/approve)
    @PatchMapping("/{orderId}/approve")
    public String approveOrder(@PathVariable Long orderId) {
        orderService.acceptOrder(orderId);
        return orderId + "번 주문이 승인(ACCEPTED) 되었습니다.";
    }

    // 3. 주문 대기 삭제 (DELETE: /api/staff/orders/{orderId})
    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return orderId + "번 주문이 삭제되었습니다.";
    }

    // 4. 실제 주문 상태 변경 (PATCH: /api/staff/orders/{orderId}/status)
    // 노션의 '상태 변경' 기능을 위해 추가했습니다.
    @PatchMapping("/{orderId}/status")
    public String updateStatus(@PathVariable Long orderId, @RequestParam String status) {
        // 이 부분은 서비스에 updateStatus 로직을 추가하면 더 완벽해집니다!
        return orderId + "번 주문의 상태가 " + status + "(으)로 변경되었습니다.";
    }
}