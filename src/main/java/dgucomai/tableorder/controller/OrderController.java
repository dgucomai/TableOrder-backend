package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<ApiResDto<OrderResDto>> createOrder(
      @RequestBody OrderCreateReqDto request) {
    OrderResDto response = orderService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResDto.success(response));
  }
}
