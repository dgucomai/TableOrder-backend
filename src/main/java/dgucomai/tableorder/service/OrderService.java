package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.entity.*;
import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.dto.req.OrderCreateReqDto;
import dgucomai.tableorder.dto.res.OrderResDto;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.repository.OrdersRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.repository.table.TableRepository;
import dgucomai.tableorder.repository.table.TableSessionRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrdersRepository orderRepository;
  private final MenuItemRepository menuItemRepository;
  private final StaffCallRepository staffCallRepository;
  private final SseEmitterManager sseEmitterManager;
  private final TableRepository tableRepository;

  @Transactional
  public void callStaff(Long tableId, Long sessionId) {
    StaffCall staffCall = new StaffCall(tableId, sessionId, "STAFF", "직원 호출");
    staffCallRepository.save(staffCall);
    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
  }

  @Transactional
  public void callDealer(Long tableId, Long sessionId) {
    StaffCall dealerCall = new StaffCall(tableId, sessionId, "DEALER", "딜러 호출");
    staffCallRepository.save(dealerCall);
    sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
  }

  @Transactional
  public OrderResDto createOrder(OrderCreateReqDto dto) {
    int total = 0;

    Tables table = tableRepository
            .findByQrToken(dto.qrToken())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 QR 토큰입니다."));

    Long tableId = table.getTableId();

    Orders order = new Orders(tableId, null, total);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
              menuItemRepository
                      .findById(itemDto.menuId())
                      .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
      OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
      order.addOrderItem(orderItem);
    }

    order.setCompletedAt(LocalDateTime.now());
    orderRepository.save(order);

    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", tableId);

    return OrderResDto.from(order);
  }

  // [수정] 6.5 명세서 및 메서드 시그니처 변경에 따라 staffId를 파라미터로 받습니다.
  @Transactional
  public void approveOrder(Long orderId, Long staffId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    if (order.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException("입금 대기 중인 주문만 승인 가능합니다.");
    }

    // [수정] 문자열 "COOKING" 대신 Enum 타입과 처리한 직원 ID를 함께 전달합니다.
    order.updateStatus(OrderStatus.COOKING);

    sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
  }

  // [수정] 6.5 명세서 및 메서드 시그니처 변경에 따라 staffId를 파라미터로 받습니다.
  @Transactional
  public void rejectOrder(Long orderId, Long staffId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    if (order.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException("입금 대기 중인 주문만 반려 가능합니다.");
    }

    order.updateStatus(OrderStatus.REJECTED);
    sseEmitterManager.sendEventToStaff("ORDER_REJECTED", order.getTableId());
  }

  @Transactional
  public void updateOrderStatus(Long orderId, String status, Long staffId) {
    Orders order =
            orderRepository
                    .findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());

    order.updateStatus(newStatus);
    order.setCompletedAt(LocalDateTime.now());
    order.setCheckedByStaffId(staffId);

    sseEmitterManager.sendEventToStaff("ORDER_STATUS_CHANGED", order.getTableId());
  }

  @Transactional
  public void resolveCall(Long callId, Long staffId) {
    StaffCall staffCall =
        staffCallRepository
            .findById(callId)
            .orElseThrow(() -> new IllegalArgumentException("호출 내역을 찾을 수 없습니다."));
    staffCall.resolve(staffId);
    sseEmitterManager.sendEventToStaff("CALL_RESOLVED", staffCall.getTableId());
  }
}
