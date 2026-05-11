package dgucomai.tableorder.service;

<<<<<<< HEAD
import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.repository.OrderRepository;
=======
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
import java.util.ArrayList;
import java.util.List;
>>>>>>> 2235135b6b6ca490eb75009ad3cd6e25b5f91218
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문 없음: " + orderId));
        order.setStatus("ACCEPTED");
    }

    @Transactional
    public void rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문 없음: " + orderId));
        order.setStatus("REJECTED");
    }
}
=======
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

  private final StaffCallRepository staffCallRepository;
  private final OrderRepository orderRepository;
  private final MenuItemRepository menuItemRepository;

  public void callStaff(Long tableId) {
    StaffCall staffCall = new StaffCall(tableId);
    staffCallRepository.save(staffCall);
  }

  public OrderResDto createOrder(OrderCreateReqDto request) {
    List<OrderCreateReqDto.OrderItemReqDto> itemRequests = request.items();
    List<MenuItems> menuItems = new ArrayList<>();
    int totalAmount = 0;

    for (OrderCreateReqDto.OrderItemReqDto itemReq : itemRequests) {
      MenuItems menuItem =
          menuItemRepository
              .findById(itemReq.menuId())
              .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
      if (menuItem.isSoldOut()) {
        throw new CustomException(
            ErrorCode.MENU_SOLD_OUT, menuItem.getMenuName() + "은(는) 현재 품절된 메뉴입니다.");
      }
      totalAmount += menuItem.getPrice() * itemReq.quantity();
      menuItems.add(menuItem);
    }

    Orders orders = new Orders(request.tableId(), totalAmount);
    for (int i = 0; i < itemRequests.size(); i++) {
      orders.addOrderItem(new OrderItems(orders, menuItems.get(i), itemRequests.get(i).quantity()));
    }
    orderRepository.save(orders);

    return OrderResDto.from(orders);
  }
}
>>>>>>> 2235135b6b6ca490eb75009ad3cd6e25b5f91218
