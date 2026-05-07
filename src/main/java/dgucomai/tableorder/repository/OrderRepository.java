package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}