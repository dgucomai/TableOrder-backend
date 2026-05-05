package dgucomai.tableorder.controller;

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
}