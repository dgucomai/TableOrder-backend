package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.req.OrderCreateReqDto;
import dgucomai.tableorder.dto.req.StaffCallReqDto;
import dgucomai.tableorder.dto.res.ApiResDto;
import dgucomai.tableorder.dto.res.OrderResDto;
import dgucomai.tableorder.service.OrderService;
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

  @PatchMapping("/staff/orders/{orderId}/status")
  public ResponseEntity<ApiResDto<Void>> updateStatus(
      @PathVariable Long orderId, @RequestBody Map<String, String> request) {
    orderService.updateOrderStatus(orderId, request.get("status"));
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_STATUS_CHANGED"));
  }

  @PostMapping("/staff-call")
  public ResponseEntity<ApiResDto<Void>> staffCall(@RequestBody StaffCallReqDto request) {
    orderService.callStaff(request.qrToken(), request.message());
    return ResponseEntity.ok(new ApiResDto<>(true, null, "STAFF_CALL_CREATED"));
  }

  @PostMapping("/dealer-call")
  public ResponseEntity<ApiResDto<Void>> dealerCall(@RequestBody StaffCallReqDto request) {
    orderService.callDealer(request.qrToken(), request.message());
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
    orderService.approveOrder(orderId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_APPROVED"));
  }

  @DeleteMapping("/staff/orders/{orderId}")
  public ResponseEntity<ApiResDto<Void>> rejectOrder(@PathVariable Long orderId) {
    orderService.rejectOrder(orderId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_REJECTED"));
  }

  @PatchMapping("/staff/calls/{callId}/resolve")
  public ResponseEntity<ApiResDto<Void>> resolveCall(
      @PathVariable Long callId, @RequestParam Long staffId) {
    orderService.resolveCall(callId, staffId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "CALL_RESOLVED"));
  }
}
