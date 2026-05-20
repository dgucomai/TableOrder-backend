package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.entity.Orders;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
  List<Orders> findBySessionId(Long sessionId);
}
