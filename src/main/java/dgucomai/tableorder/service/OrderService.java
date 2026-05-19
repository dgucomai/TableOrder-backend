package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.*;
import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.repository.*;
import dgucomai.tableorder.sse.SseEmitterManager;
import jakarta.persistence.EntityManager;
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
  private final EntityManager em;

  private Object[] getValidTableInfo(String qrToken) {
    return (Object[])
        em.createQuery(
                "SELECT t.tableId, t.currentSession.sessionId FROM Tables t WHERE t.qrToken = :qrToken")
            .setParameter("qrToken", qrToken)
            .getSingleResult();
  }

  @Transactional
  public void callStaff(String qrToken, String message) {
    Object[] info = getValidTableInfo(qrToken);
    Long tableId = (Long) info[0];
    Long sessionId = (Long) info[1];

    StaffCall call = new StaffCall(tableId, sessionId, "STAFF", message);
    staffCallRepository.save(call);
    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
  }

  @Transactional
  public void callDealer(String qrToken, String message) {
    Object[] info = getValidTableInfo(qrToken);
    Long tableId = (Long) info[0];
    Long sessionId = (Long) info[1];

    StaffCall call = new StaffCall(tableId, sessionId, "DEALER", message);
    staffCallRepository.save(call);
    sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
  }

  @Transactional
  public OrderResDto createOrder(OrderCreateReqDto dto) {
    Object[] info = getValidTableInfo(dto.qrToken());
    Long tableId = (Long) info[0];
    Long sessionId = (Long) info[1];

    Orders order = new Orders(tableId, sessionId, 0);
    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
          menuItemRepository
              .findById(itemDto.menuId())
              .orElseThrow(() -> new IllegalArgumentException("메뉴 없음"));
      order.addOrderItem(new OrderItems(order, menu, itemDto.quantity()));
    }

    orderRepository.save(order);
    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", tableId);
    return OrderResDto.from(order);
  }

  @Transactional
  public void approveOrder(Long orderId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    if (order.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException("입금 대기 중인 주문만 승인 가능합니다.");
    }

    order.updateStatus("COOKING");
    sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
  }

  @Transactional
  public void rejectOrder(Long orderId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    if (order.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException("입금 대기 중인 주문만 반려 가능합니다.");
    }

    order.updateStatus("REJECTED");
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
  public void resolveCall(Long callId, Long staffId) {
    StaffCall staffCall =
        staffCallRepository
            .findById(callId)
            .orElseThrow(() -> new IllegalArgumentException("호출 내역을 찾을 수 없습니다."));

    staffCall.resolve(staffId);
    sseEmitterManager.sendEventToStaff("CALL_RESOLVED", staffCall.getTableId());
  }
}
