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
    staffCall.setCallType("STAFF");
    staffCallRepository.save(staffCall);
    sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
  }

  @Transactional
  public void callDealer(Long tableId) {
    StaffCall dealerCall = new StaffCall(tableId);
    dealerCall.setCallType("DEALER");
    staffCallRepository.save(dealerCall);
    sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
  }

  @Transactional
  public OrderResDto createOrder(OrderCreateReqDto dto) {
    int total = 0;
    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu =
          menuItemRepository
              .findById(itemDto.menuId())
              .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

      if (menu.isSoldOut()) {
        throw new CustomException(ErrorCode.MENU_SOLD_OUT, menu.getMenuName() + "은(는) 품절되었습니다.");
      }
      total += (menu.getPrice() * itemDto.quantity());
    }

    Orders order = new Orders(dto.tableId(), total);

    for (OrderCreateReqDto.OrderItemReqDto itemDto : dto.items()) {
      MenuItems menu = menuItemRepository.findById(itemDto.menuId()).get();
      OrderItems orderItem = new OrderItems(order, menu, itemDto.quantity());
      order.addOrderItem(orderItem);
    }

    orderRepository.save(order);

    sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", dto.tableId());

    return OrderResDto.from(order);
  }
}
