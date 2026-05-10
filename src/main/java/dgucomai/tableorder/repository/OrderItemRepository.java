package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {}
