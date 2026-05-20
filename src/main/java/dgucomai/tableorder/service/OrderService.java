package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.MenuItems;
import dgucomai.tableorder.domain.OrderItems;
import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.repository.OrdersRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
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

    // [수정] Orders 생성자 인자 개수에 맞게 가운데에 null(changed_by 없음)을 삽입합니다.
    Orders order = new Orders(dto.tableId(), null, total);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
          menuItemRepository
              .findById(itemDto.menuId())
              .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
      OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
      order.addOrderItem(orderItem);
    }

    // 기본 주문 정보 기입 후 저장
    order.setCompletedAt(LocalDateTime.now());

    orderRepository.save(order);

    // 주점 관리자 화면(Staff용 웹)으로 주문 알림 SSE 발송
    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", dto.tableId());

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
    order.updateStatus(OrderStatus.COOKING, staffId);
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

    // [수정] 문자열 "REJECTED" 대신 Enum 타입과 처리한 직원 ID를 함께 전달합니다.
    order.updateStatus(OrderStatus.REJECTED, staffId);
    sseEmitterManager.sendEventToStaff("ORDER_REJECTED", order.getTableId());
  }

  // [수정] 6.5 명세서 및 메서드 시그니처 변경에 따라 staffId를 파라미터로 받습니다.
  @Transactional
  public void updateOrderStatus(Long orderId, String status, Long staffId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    // [수정] 들어온 외부 문자열 status를 OrderStatus Enum 타입으로 변환하고 staffId를 함께 전달합니다.
    order.updateStatus(OrderStatus.valueOf(status), staffId);
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
