package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.req.OrderCreateReqDto;
import dgucomai.tableorder.dto.req.StaffCallReqDto;
import dgucomai.tableorder.dto.res.ApiResDto;
import dgucomai.tableorder.dto.res.OrderResDto;
import dgucomai.tableorder.dto.res.TableNumResDto;
import dgucomai.tableorder.service.OrderService;
import dgucomai.tableorder.service.table.TableService;
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
  private final TableService tableService;

  @GetMapping("/qtnum")
  public ResponseEntity<ApiResDto<TableNumResDto>> getTableNumByQt(@RequestParam String qt) {
    TableNumResDto response = tableService.getTableNumByQrToken(qt);
    return ResponseEntity.ok(ApiResDto.success(response));
  }

  public record OrderStatusData(
      Long orderId,
      String orderStatus,
      String completedAt,
      int changedByStaffId,
      String changedByStaffName) {}

  @PostMapping("/orders")
  public ResponseEntity<ApiResDto<OrderResDto>> createOrder(
      @RequestBody OrderCreateReqDto request) {
    try {
      OrderResDto response = orderService.createOrder(request);
      return new ResponseEntity<>(
          new ApiResDto<>(true, response, "PAYMENT_REQUEST_CREATED"), HttpStatus.CREATED);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  @PostMapping("/staff-call")
  public ResponseEntity<ApiResDto<Void>> staffCall(@RequestBody StaffCallReqDto request) {
    try {
      orderService.callStaff(request.qrToken(), request.message());
      return ResponseEntity.ok(new ApiResDto<>(true, null, "STAFF_CALL_CREATED"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  @PostMapping("/dealer-call")
  public ResponseEntity<ApiResDto<Void>> dealerCall(@RequestBody StaffCallReqDto request) {
    try {
      orderService.callDealer(request.qrToken(), request.message());
      return ResponseEntity.ok(new ApiResDto<>(true, null, "DEALER_CALL_CREATED"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  @PatchMapping("/staff/orders/{orderId}/approve")
  public ResponseEntity<ApiResDto<Void>> approveOrder(
      @PathVariable Long orderId, @RequestParam Long staffId) {
    try {
      orderService.approveOrder(orderId, staffId);
      return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_APPROVED"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  @PatchMapping("/staff/orders/{orderId}/status")
  public ResponseEntity<ApiResDto<Void>> updateStatus(
      @PathVariable Long orderId, @RequestBody Map<String, String> request) {
    try {
      orderService.updateOrderStatus(orderId, request.get("status"));
      return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_STATUS_CHANGED"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  @PatchMapping("/staff/calls/{callId}/resolve")
  public ResponseEntity<ApiResDto<Void>> resolveCall(
      @PathVariable Long callId, @RequestParam Long staffId) {
    try {
      orderService.resolveCall(callId, staffId);
      return ResponseEntity.ok(new ApiResDto<>(true, null, "CALL_RESOLVED"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  @DeleteMapping("/staff/orders/{orderId}")
  public ResponseEntity<ApiResDto<Void>> rejectOrder(
      @PathVariable Long orderId, @RequestParam Long staffId) {
    try {
      orderService.rejectOrder(orderId, staffId);
      return ResponseEntity.ok(new ApiResDto<>(true, null, "ORDER_REJECTED"));
    } catch (Exception e) {
      return handleException(e);
    }
  }

  private <T> ResponseEntity<ApiResDto<T>> handleException(Exception e) {
    String message = e.getMessage() != null ? e.getMessage() : "500 INTERNAL_SERVER_ERROR";

    if (message.startsWith("404")) {
      return new ResponseEntity<>(new ApiResDto<>(false, null, message), HttpStatus.NOT_FOUND);
    }

    if (message.startsWith("400")) {
      return new ResponseEntity<>(new ApiResDto<>(false, null, message), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(new ApiResDto<>(false, null, message), HttpStatus.BAD_REQUEST);
  }
}
