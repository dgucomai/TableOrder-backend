package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.OrderRequestDto;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/staff-call")
    public ResponseEntity<String> staffCall(@RequestParam Long tableId) {
        orderService.callStaff(tableId);

        return ResponseEntity.ok(tableId + "번 테이블의 호출이 접수되었습니다.");
    }

    @PostMapping("/orders")
    public ResponseEntity<String> registerOrder(@RequestBody OrderRequestDto dto) {
        Long orderId = orderService.registerOrder(dto);
        return ResponseEntity.ok(dto.getTableId() + "번 테이블 주문 대기 등록 완료! (주문번호: " + orderId + ")");
    }
}