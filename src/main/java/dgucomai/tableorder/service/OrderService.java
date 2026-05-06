package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        if (order.getStatus() != Order.OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("결제 대기 중인 주문만 승인할 수 있습니다");
        }

        order.setStatus(Order.OrderStatus.APPROVED);
        return orderRepository.save(order);
    }

    public Order rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        if (order.getStatus() == Order.OrderStatus.DONE ||
                order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 완료되거나 취소된 주문은 거절할 수 없습니다");
        }

        order.setStatus(Order.OrderStatus.REJECTED);
        return orderRepository.save(order);
    }
}
