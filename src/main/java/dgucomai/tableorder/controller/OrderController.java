package dgucomai.tableorder.controller;

<<<<<<< HEAD
import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    @PatchMapping("/{orderId}/approve")
    public String approve(@PathVariable Long orderId) {
        orderService.approveOrder(orderId);
        return "Order " + orderId + " approved.";
    }

    @PatchMapping("/{orderId}/reject")
    public String reject(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);
        return "Order " + orderId + " rejected.";
    }
}
=======
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
>>>>>>> 2235135b6b6ca490eb75009ad3cd6e25b5f91218
