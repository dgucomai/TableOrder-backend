package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.MenuItems;
import dgucomai.tableorder.domain.OrderItems;
import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.exception.CustomException;
import dgucomai.tableorder.exception.ErrorCode;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.repository.OrderRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import dgucomai.tableorder.repository.OrdersRepository;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final MenuItemRepository menuItemRepository;
  private final StaffCallRepository staffCallRepository;
  private final SseEmitterManager sseEmitterManager;

  @Transactional
  public void callStaff(Long tableId) {
    StaffCall staffCall = new StaffCall(tableId, "STAFF_CALL");
    staffCallRepository.save(staffCall);
    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
  }

  @Transactional
  public void rejectOrder(Long orderId) {
      Orders order = orderRepository.findById(orderId)
              .orElseThrow(() -> new RuntimeException("주문 없음"));
      order.setOrderStatus(Orders.OrderStatus.REJECTED);
      order.setPaymentStatus(Orders.PaymentStatus.REJECTED);
      order.setCheckedAt(LocalDateTime.now());
      order.setCheckedByStaffId(2L);
      order.setCheckedByStaffName("김직원");
  }

  @Transactional
  public void callDealer(Long tableId) {
    StaffCall dealerCall = new StaffCall(tableId, "DEALER_CALL");
    staffCallRepository.save(dealerCall);
    sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
  }

  @Transactional
  public void updateOrderStatus(Long orderId, String status) {
      Orders order = orderRepository.findById(orderId)
              .orElseThrow(() -> new RuntimeException("주문 번호 " + orderId + "번 없음"));

      if ("COMPLETED".equalsIgnoreCase(status)) {
          order.setOrderStatus(Orders.OrderStatus.COMPLETED);
      } else if ("CANCELLED".equalsIgnoreCase(status)) {
          order.setOrderStatus(Orders.OrderStatus.CANCELLED);
      }

      public OrderResDto createOrder(OrderCreateReqDto dto) {
        int total = 0;
//      Orders order = new Orders(dto.tableId(), total);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
          menuItemRepository
              .findById(itemDto.menuId())
              .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
      OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
      order.addOrderItem(orderItem);
    }
        for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
            MenuItems menu = menuItemRepospackage dgucomai.tableorder.service;

            @Service
            @RequiredArgsConstructor
            @Transactional(readOnly = true)
            public class OrderService {

                private final OrdersRepository ordersRepository;
                private final StaffCallRepository staffCallRepository;
                private final SseEmitterManager sseEmitterManager;

                // 1. 주문 승인 (SSE 알림 포함)
                @Transactional
                public void approveOrder(Long orderId) {
                    var order = ordersRepository.findById(orderId)
                            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

                    order.setOrderStatus(Orders.OrderStatus.COOKING);
                    order.setPaymentStatus(Orders.PaymentStatus.APPROVED);
                    order.setCheckedAt(LocalDateTime.now());

                    // [병합 기능] 선배님들의 SSE 알림 발송
                    sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
                }

                // 2. 주문 반려
                @Transactional
                public void rejectOrder(Long orderId) {
                    var order = ordersRepository.findById(orderId)
                            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

                    order.setOrderStatus(Orders.OrderStatus.REJECTED);
                    order.setPaymentStatus(Orders.PaymentStatus.REJECTED);
                }

                // 3. 상태 변경 (Java 21 Switch 활용)
                @Transactional
                public void updateOrderStatus(Long orderId, String status) {
                    var order = ordersRepository.findById(orderId)
                            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

                    switch (status.toUpperCase()) {
                        case "COMPLETED" -> {
                            order.setOrderStatus(Orders.OrderStatus.COMPLETED);
                            order.setCompletedAt(LocalDateTime.now());
                            sseEmitterManager.sendEventToStaff("ORDER_COMPLETED", order.getTableId());
                        }
                        case "CANCELLED" -> order.setOrderStatus(Orders.OrderStatus.CANCELLED);
                        default -> order.setOrderStatus(Orders.OrderStatus.valueOf(status.toUpperCase()));
                    }
                }

                // 4. 선배님들 기능: 직원/딜러 호출
                @Transactional
                public void callStaff(Long tableId) {
                    staffCallRepository.save(new StaffCall(tableId, "STAFF_CALL"));
                    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
                }

                @Transactional
                public void callDealer(Long tableId) {
                    staffCallRepository.save(new StaffCall(tableId, "DEALER_CALL"));
                    sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
                }
            }itory.findById(itemDto.menuId()).get();
            OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
            order.addOrderItem(orderItem);
        }

        order.setCompletedAt(LocalDateTime.now());
        order.setCheckedByStaffId(2L);
        order.setCheckedByStaffName("김직원");
    }
    orderRepository.save(order);
    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", dto.tableId());

    return OrderResDto.from(order);
  }

  @Transactional
  public void approveOrder(Long orderId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    order.updateStatus("COOKING");
    sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
  }

  @Transactional
  public void rejectOrder(Long orderId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    orderRepository.delete(order);
    sseEmitterManager.sendEventToStaff("ORDER_REJECTED", order.getTableId());
  }

  @Transactional
  public void updateOrderStatus(Long orderId, String status) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    order.updateStatus(status);
    sseEmitterManager.sendEventToStaff("ORDER_STATUS_CHANGED", order.getTableId());
  }

  @Transactional
  public void resolveCall(Long callId) {
    StaffCall staffCall =
        staffCallRepository
            .findById(callId)
            .orElseThrow(() -> new IllegalArgumentException("호출 내역을 찾을 수 없습니다."));

    staffCall.resolve();
    sseEmitterManager.sendEventToStaff("CALL_RESOLVED", staffCall.getTableId());
  }
}
