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
    orderService.approveOrder(orderId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_APPROVED"));
  }

  @DeleteMapping("/staff/orders/{orderId}")
  public ResponseEntity<ApiResDto<Void>> rejectOrder(@PathVariable Long orderId) {
    orderService.rejectOrder(orderId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_REJECTED"));
  }

  @PatchMapping("/staff/orders/{orderId}/status")
  public ResponseEntity<ApiResDto<Void>> updateOrderStatus(
          @PathVariable Long orderId, @RequestParam String status) {
    orderService.updateOrderStatus(orderId, status);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_STATUS_CHANGED"));
  }

  @PatchMapping("/staff/calls/{callId}/resolve")
  public ResponseEntity<ApiResDto<Void>> resolveCall(
          @PathVariable Long callId, @RequestParam Long staffId) {
    orderService.resolveCall(callId, staffId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "CALL_RESOLVED"));
  }
}