package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.service.OrderService;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  public record OrderStatusData(
      Long orderId,
      String orderStatus,
      String completedAt,
      int changedByStaffId,
      String changedByStaffName) {}

  @PatchMapping("/staff/orders/{orderId}/status")
  public ResponseEntity<ApiResDto<OrderStatusData>> updateStatus(
      @PathVariable Long orderId, @RequestBody Map<String, String> request) {

    String newStatus = request.get("status");

    // [수정] 아래 하드코딩된 더미 직원 ID(2번)와 발 맞춰서 서비스에 2L을 전달합니다.
    // 추후 로그인 기능이 붙으면 이 자리에 실제 로그인한 직원 ID를 넣어주시면 됩니다!
    Long dummyStaffId = 2L;
    orderService.updateOrderStatus(orderId, newStatus, dummyStaffId);

    var data =
        new OrderStatusData(
            orderId,
            newStatus.toUpperCase(),
            "COMPLETED".equalsIgnoreCase(newStatus) ? LocalDateTime.now().toString() : null,
            2,
            "김직원");

    return ResponseEntity.ok(new ApiResDto<>(true, data, "ORDER_STATUS_CHANGED"));
  }

  @PostMapping("/staff-call")
  public ResponseEntity<ApiResDto<Void>> staffCall(
      @RequestParam Long tableId, @RequestParam Long sessionId) {
    orderService.callStaff(tableId, sessionId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "STAFF_CALL_CREATED"));
  }

  @PostMapping("/dealer-call")
  public ResponseEntity<ApiResDto<Void>> dealerCall(
      @RequestParam Long tableId, @RequestParam Long sessionId) {
    orderService.callDealer(tableId, sessionId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "DEALER_CALL_CREATED"));
  }

  @PostMapping("/orders")
  public ResponseEntity<ApiResDto<OrderResDto>> createOrder(
      @RequestBody OrderCreateReqDto request) {
    OrderResDto response = orderService.createOrder(request);
    return new ResponseEntity<>(
        new ApiResDto<>(true, response, "PAYMENT_REQUEST_CREATED"), HttpStatus.CREATED);
  }

  @PatchMapping("/staff/orders/{orderId}/approve")
  public ResponseEntity<ApiResDto<Void>> approveOrder(@PathVariable Long orderId) {
    // [수정] 서비스가 요구하는 staffId 인자 규칙에 맞게 임시 더미 ID(2L)를 넘겨줍니다.
    orderService.approveOrder(orderId, 2L);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_APPROVED"));
  }

  @DeleteMapping("/staff/orders/{orderId}")
  public ResponseEntity<ApiResDto<Void>> rejectOrder(@PathVariable Long orderId) {
    // [수정] 서비스가 요구하는 staffId 인자 규칙에 맞게 임시 더미 ID(2L)를 넘겨줍니다.
    orderService.rejectOrder(orderId, 2L);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_REJECTED"));
  }

  @PatchMapping("/staff/calls/{callId}/resolve")
  public ResponseEntity<ApiResDto<Void>> resolveCall(
      @PathVariable Long callId, @RequestParam Long staffId) {
    orderService.resolveCall(callId, staffId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "CALL_RESOLVED"));
  }
}
