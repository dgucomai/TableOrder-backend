package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {}
