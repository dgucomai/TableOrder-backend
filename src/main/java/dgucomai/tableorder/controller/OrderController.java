package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.OrderApproveResDto;
import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PatchMapping("/{orderId}/approve")
    public ApiResDto<OrderApproveResDto> approveOrder(@PathVariable Long orderId) {
        Orders order = orderService.approveOrder(orderId);

        OrderApproveResDto resDto = new OrderApproveResDto(
                order.getOrderId(),
                order.getOrderStatus().name(),
                "APPROVED",
                order.getCreatedAt().toString(),
                1L,
                "관리자"
        );

        return ApiResDto.success("ORDER_APPROVED", "주문이 승인되었습니다.", resDto);
    }
}