package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 전체 조회
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    // 1. 주문 승인 (APPROVED로 변경)[cite: 1]
    public void approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));
        order.setStatus(Order.OrderStatus.APPROVED); // 기획서 명칭 적용[cite: 1]
    }

    // 2. 주문 반려 (REJECTED로 변경)[cite: 1]
    public void rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));
        order.setStatus(Order.OrderStatus.REJECTED); // 기획서 명칭 적용[cite: 1]
    }

    // 3. 주문 삭제 (기능 유지)
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}