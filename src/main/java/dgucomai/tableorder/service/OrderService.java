package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.entity.*;
import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.dto.req.OrderCreateReqDto;
import dgucomai.tableorder.dto.res.OrderResDto;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.repository.OrdersRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
    try {
      return (Object[])
          em.createQuery(
                  "SELECT t.tableId, t.currentSessionId FROM Tables t WHERE t.qrToken = :qrToken")
              .setParameter("qrToken", qrToken)
              .getSingleResult();
    } catch (NoResultException e) {
      throw new IllegalArgumentException("404 TABLE_NOT_FOUND");
    }
  }

  private void activateSessionIfNeeded(Long sessionId) {
    em.createQuery(
            "UPDATE TableSession ts "
                + "SET ts.status = :activeStatus, ts.startedAt = CURRENT_TIMESTAMP "
                + "WHERE ts.sessionId = :sessionId AND ts.status = :closedStatus")
        .setParameter("activeStatus", TableSessionStatus.ACTIVE)
        .setParameter("closedStatus", TableSessionStatus.CLOSED)
        .setParameter("sessionId", sessionId)
        .executeUpdate();
  }

  private void createPaymentRequest(Orders order) {
    PaymentRequest paymentRequest = new PaymentRequest(order.getOrderId());
    em.persist(paymentRequest);
  }

  @Transactional
  public OrderResDto createOrder(OrderCreateReqDto dto) {
    Object[] info = getValidTableInfo(dto.qrToken());

    Long tableId = ((Number) info[0]).longValue();
    Long sessionId = (info[1] != null) ? ((Number) info[1]).longValue() : null;

    if (sessionId == null) {
      throw new IllegalArgumentException("404 TABLE_SESSION_NOT_FOUND");
    }

    activateSessionIfNeeded(sessionId);

    TableSession tableSession = em.find(TableSession.class, sessionId);
    if (tableSession == null) {
      throw new IllegalArgumentException("404 TABLE_SESSION_NOT_FOUND");
    }

    Orders order = new Orders(tableId, tableSession.getSessionId(), 0);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
          menuItemRepository
              .findById(itemDto.menuId())
              .orElseThrow(() -> new IllegalArgumentException("404 MENU_NOT_FOUND"));

      if (menu.isSoldOut()) {
        throw new IllegalStateException("400 MENU_SOLD_OUT");
      }
      order.addOrderItem(new OrderItems(order, menu, itemDto.quantity()));
    }

    orderRepository.save(order);
    createPaymentRequest(order);
    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", tableId);

    return OrderResDto.from(order);
  }

  @Transactional
  public void callStaff(String qrToken, String message) {
    Object[] info = getValidTableInfo(qrToken);
    Long tableId = ((Number) info[0]).longValue();
    Long sessionId = (info[1] != null) ? ((Number) info[1]).longValue() : null;

    if (sessionId == null) {
      throw new IllegalArgumentException("404 TABLE_SESSION_NOT_FOUND");
    }

    activateSessionIfNeeded(sessionId);

    StaffCall call = new StaffCall(tableId, sessionId, "STAFF", message);
    staffCallRepository.save(call);
    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
  }

  @Transactional
  public void callDealer(String qrToken, String message) {
    Object[] info = getValidTableInfo(qrToken);
    Long tableId = ((Number) info[0]).longValue();
    Long sessionId = (info[1] != null) ? ((Number) info[1]).longValue() : null;

    if (sessionId == null) {
      throw new IllegalArgumentException("404 TABLE_SESSION_NOT_FOUND");
    }

    activateSessionIfNeeded(sessionId);

    StaffCall call = new StaffCall(tableId, sessionId, "DEALER", message);
    staffCallRepository.save(call);
    sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
  }

  @Transactional
  public void approveOrder(Long orderId, Long staffId) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

    if (order.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
      throw new IllegalStateException("입금 대기 중인 주문만 승인 가능합니다.");
    }

    order.updateStatus(OrderStatus.COOKING);

    try {
      PaymentRequest paymentRequest =
          em.createQuery(
                  "SELECT pr FROM PaymentRequest pr WHERE pr.orderId = :orderId",
                  PaymentRequest.class)
              .setParameter("orderId", orderId)
              .getSingleResult();
      paymentRequest.approve(staffId);
    } catch (NoResultException e) {
    }

    sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
  }

  @Transactional
  public void updateOrderStatus(Long orderId, String status) {
    Orders order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("404 ORDER_NOT_FOUND"));

    order.updateStatus(OrderStatus.valueOf(status.toUpperCase()));
    sseEmitterManager.sendEventToStaff("ORDER_STATUS_CHANGED", order.getTableId());
  }

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

    try {
      PaymentRequest paymentRequest =
          em.createQuery(
                  "SELECT pr FROM PaymentRequest pr WHERE pr.orderId = :orderId",
                  PaymentRequest.class)
              .setParameter("orderId", orderId)
              .getSingleResult();
      paymentRequest.reject(staffId);
    } catch (NoResultException e) {
    }

    sseEmitterManager.sendEventToStaff("ORDER_REJECTED", order.getTableId());
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
