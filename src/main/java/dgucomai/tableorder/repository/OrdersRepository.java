package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.entity.Orders;
import dgucomai.tableorder.domain.enums.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
  boolean existsBySessionIdAndOrderStatus(Long sessionId, OrderStatus orderStatus);

  List<Orders> findBySessionId(Long sessionId);
}
