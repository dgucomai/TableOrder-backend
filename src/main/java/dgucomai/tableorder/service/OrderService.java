package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.Order;
import dgucomai.tableorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 전체 조회 (대기 중인 주문 확인용)
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    // 주문 승인 (상태를 ACCEPTED로 변경)
    @Transactional
    public void acceptOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다. id=" + orderId));
        order.setStatus("ACCEPTED");
    }

    // 주문 삭제 (거절 또는 취소)
    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}