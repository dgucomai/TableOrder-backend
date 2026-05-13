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
  public ResponseEntity<ApiResDto<Void>> staffCall(@RequestParam Long tableId) {
    orderService.callStaff(tableId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "STAFF_CALL_CREATED"));
  }

  @PostMapping("/dealer-call")
  public ResponseEntity<ApiResDto<Void>> dealerCall(@RequestParam Long tableId) {
    orderService.callDealer(tableId);
    return ResponseEntity.ok(new ApiResDto<>(true, null, "DEALER_CALL_CREATED"));
  }

  @PostMapping("/orders")
  public ResponseEntity<ApiResDto<OrderResDto>> createOrder(
          @RequestBody OrderCreateReqDto request) {
    OrderResDto response = orderService.createOrder(request);

    return new ResponseEntity<>(
            new ApiResDto<>(true, response, "PAYMENT_REQUEST_CREATED"),
            HttpStatus.CREATED
    );
  }
}