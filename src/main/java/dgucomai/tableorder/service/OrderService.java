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
  @Transactional
  public OrderResDto createOrder(OrderCreateReqDto dto) {
    int total = 0;
    Orders order = new Orders(dto.tableId(), total);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu = menuItemRepository.findById(itemDto.menuId()).get();
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
}
