package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;

    @Transactional
    public void approveOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));
        order.setOrderStatus(Orders.OrderStatus.COOKING);
        order.setPaymentStatus(Orders.PaymentStatus.APPROVED);
        order.setCheckedAt(LocalDateTime.now());
        order.setCheckedByStaffId(101L);
        order.setCheckedByStaffName("관리자");
    }

    @Transactional
    public void rejectOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));
        order.setOrderStatus(Orders.OrderStatus.REJECTED);
        order.setPaymentStatus(Orders.PaymentStatus.REJECTED);
        order.setCheckedAt(LocalDateTime.now());
        order.setCheckedByStaffId(2L);
        order.setCheckedByStaffName("김직원");
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 번호 " + orderId + "번 없음"));

        if ("COMPLETED".equalsIgnoreCase(status)) {
            order.setOrderStatus(Orders.OrderStatus.COMPLETED);
        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            order.setOrderStatus(Orders.OrderStatus.CANCELLED);
        }

        order.setCompletedAt(LocalDateTime.now());
        order.setCheckedByStaffId(2L);
        order.setCheckedByStaffName("김직원");
    }
}