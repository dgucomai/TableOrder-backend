package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.MenuItems;
import dgucomai.tableorder.domain.OrderItems;
import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.repository.OrderRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
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
    Orders order = new Orders(dto.tableId(), total);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu = menuItemRepository.findById(itemDto.menuId())
              .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
      OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
      order.addOrderItem(orderItem);
    }

    orderRepository.save(order);
    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", dto.tableId());

    return OrderResDto.from(order);
  }

  @Transactional
  public void approveOrder(Long orderId) {
    Orders order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    order.updateStatus("COOKING");
    sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
  }

  @Transactional
  public void updateOrderStatus(Long orderId, String status) {
    Orders order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    order.updateStatus(status);
    sseEmitterManager.sendEventToStaff("ORDER_STATUS_CHANGED", order.getTableId());
  }

  @Transactional
  public void resolveCall(Long callId, Long staffId) {
    StaffCall staffCall = staffCallRepository.findById(callId)
            .orElseThrow(() -> new IllegalArgumentException("호출 내역을 찾을 수 없습니다."));
    staffCall.resolve(staffId);
    sseEmitterManager.sendEventToStaff("CALL_RESOLVED", staffCall.getTableId());
  }
}