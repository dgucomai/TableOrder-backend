package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Orders> findAllOrders() { // 👈 여기도 Orders
        return orderRepository.findAll();
    }

    @Transactional
    public Orders approveOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문 없음: " + orderId));
        order.updateStatus(OrderStatus.APPROVED);

        return order;
    }

    @Transactional
    public void rejectOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문 없음: " + orderId));
        order.updateStatus(OrderStatus.REJECTED);
    }
}