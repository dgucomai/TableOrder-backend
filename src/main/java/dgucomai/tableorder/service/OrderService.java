package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.MenuItems;
import dgucomai.tableorder.domain.OrderItems;
import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.dto.OrderCreateReqDto;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.repository.OrderRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
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

    /**
     * 1. 신규 주문 생성 (소비자 태블릿)
     */
    @Transactional
    public OrderResDto createOrder(OrderCreateReqDto dto) {
        // 초기 총액 0으로 주문 객체 생성
        var order = new Orders(dto.tableId(), 0);

        // 충돌로 인해 쪼개졌던 menuItemRepository 루프 통합 및 정상화
        for (var itemDto : dto.items()) {
            MenuItems menu = menuItemRepository.findById(itemDto.menuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

            var orderItem = new OrderItems(order, menu, itemDto.quantity());
            order.addOrderItem(orderItem);
        }

        // 기본 주문 정보 기입 후 저장
        order.setCompletedAt(LocalDateTime.now());

        orderRepository.save(order);

        // 주점 관리자 화면(Staff용 웹)으로 주문 알림 SSE 발송
        sseEmitterManager.sendEventToStaff("PAYMENT_REQUEST_CREATED", dto.tableId());

        return OrderResDto.from(order);
    }

    /**
     * 2. 주문 승인 (관리자가 확인하여 조리 시작)
     */
    @Transactional
    public void approveOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 상태값 세팅 방식 통일
        order.setOrderStatus(Orders.OrderStatus.COOKING);
        order.setPaymentStatus(Orders.PaymentStatus.APPROVED);
        order.setCheckedAt(LocalDateTime.now());

        sseEmitterManager.sendEventToStaff("ORDER_APPROVED", order.getTableId());
    }

    /**
     * 3. 주문 반려/거절 (재고 부족 등의 사유)
     */
    @Transactional
    public void rejectOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 데이터를 완전히 Delete할지, 상태만 REJECTED로 바꿀지 중 상태 변경으로 통합 처리
        order.setOrderStatus(Orders.OrderStatus.REJECTED);
        order.setPaymentStatus(Orders.PaymentStatus.REJECTED);
        order.setCheckedAt(LocalDateTime.now());

        sseEmitterManager.sendEventToStaff("ORDER_REJECTED", order.getTableId());
    }

    /**
     * 4. 주문 상태 동적 변경 (서빙 완료 / 취소 등 - Java 21 Switch 활용)
     */
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 가독성이 높은 Java 21 화살표(->) Switch 구문으로 분기 분리
        switch (status.toUpperCase()) {
            case "COMPLETED" -> {
                order.setOrderStatus(Orders.OrderStatus.COMPLETED);
                order.setCompletedAt(LocalDateTime.now());
                sseEmitterManager.sendEventToStaff("ORDER_COMPLETED", order.getTableId());
            }
            case "CANCELLED" -> {
                order.setOrderStatus(Orders.OrderStatus.CANCELLED);
                sseEmitterManager.sendEventToStaff("ORDER_CANCELLED", order.getTableId());
            }
            default -> {
                try {
                    order.setOrderStatus(Orders.OrderStatus.valueOf(status.toUpperCase()));
                    sseEmitterManager.sendEventToStaff("ORDER_STATUS_CHANGED", order.getTableId());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("잘못된 주문 상태 값입니다: " + status);
                }
            }
        }
    }

    /**
     * 5. 일반 직원 호출 (물, 컵 등)
     */
    @Transactional
    public void callStaff(Long tableId) {
        var staffCall = new StaffCall(tableId, "STAFF_CALL");
        staffCallRepository.save(staffCall);
        sseEmitterManager.sendEventToStaff("STAFF_CALL_CREATED", tableId);
    }

    /**
     * 6. 딜러 호출
     */
    @Transactional
    public void callDealer(Long tableId) {
        var dealerCall = new StaffCall(tableId, "DEALER_CALL");
        staffCallRepository.save(dealerCall);
        sseEmitterManager.sendEventToStaff("DEALER_CALL_CREATED", tableId);
    }

    /**
     * 7. 테이블 호출 해결 완료 (직원이 처리 완료 버튼 누름)
     */
    @Transactional
    public void resolveCall(Long callId) {
        var staffCall = staffCallRepository.findById(callId)
                .orElseThrow(() -> new IllegalArgumentException("호출 내역을 찾을 수 없습니다."));

        staffCall.resolve();
        sseEmitterManager.sendEventToStaff("CALL_RESOLVED", staffCall.getTableId());
    }
}