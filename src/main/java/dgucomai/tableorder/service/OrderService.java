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
import java.lang.reflect.Field;
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
  public void callStaff(Long tableId) {
    StaffCall staffCall = new StaffCall(tableId);
    staffCallRepository.save(staffCall);
    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
  }

  @Transactional
  public OrderResDto createOrder(OrderCreateReqDto dto) {
    Orders order = new Orders(dto.tableId(), 0);

    int calculatedTotalAmount = 0;

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
          menuItemRepository
              .findById(itemDto.menuId())
              .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

      OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
      order.getOrderItems().add(orderItem);
      calculatedTotalAmount += orderItem.getSubtotal();
    }

    try {
      Field field = Orders.class.getDeclaredField("totalAmount");
      field.setAccessible(true);
      field.set(order, calculatedTotalAmount);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    orderRepository.save(order);

    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", order.getOrderId());

    return OrderResDto.from(order);
    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", request.tableId());

    return OrderResDto.from(orders);
  }
}
