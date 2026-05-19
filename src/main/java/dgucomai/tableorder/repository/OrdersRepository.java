package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
  boolean existsBySessionIdAndPaymentStatus(Long sessionId, PaymentStatus paymentStatus);

  // ⭐️ 이 메서드를 새로 추가해 주세요! 현재 세션의 모든 주문 목록을 가져옵니다.
  List<Orders> findBySessionId(Long sessionId);
}
