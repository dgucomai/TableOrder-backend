package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 이 한 줄만으로 DB에서 주문을 찾고, 저장하고, 삭제하는 모든 기능이 완성됩니다!
}