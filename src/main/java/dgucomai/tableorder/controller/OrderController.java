package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.StaffCallReqDto;
import dgucomai.tableorder.service.OrderService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping("/staff-call")
  public ResponseEntity<Map<String, Object>> staffCall(@RequestBody StaffCallReqDto dto) {
    orderService.callStaff(dto);

    return ResponseEntity.ok(
        Map.of(
            "success",
            true,
            "code",
            "STAFF_CALL_CREATED",
            "message",
            "직원 호출이 접수되었습니다.",
            "data",
            Map.of()));
  }
}
