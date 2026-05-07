package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.repository.OrderRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.domain.OrderItem;
import dgucomai.tableorder.dto.OrderRequestDto;
import dgucomai.tableorder.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final StaffCallRepository staffCallRepository;
    private final OrderRepository orderRepository;
    public void callStaff(Long tableId) {
        StaffCall staffCall = new StaffCall(tableId);

        staffCallRepository.save(staffCall);
    }

    @Transactional
    public Long registerOrder(OrderRequestDto dto) {
        Order order = new Order(dto.getTableId());

        for (OrderItemDto itemDto : dto.getItems()) {
            OrderItem item = new OrderItem(itemDto.getMenuName(), itemDto.getQuantity(), itemDto.getPrice());
            order.addOrderItem(item);
        }

        return orderRepository.save(order).getId();
    }
}