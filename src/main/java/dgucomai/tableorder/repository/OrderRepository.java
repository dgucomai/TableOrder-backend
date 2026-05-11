package dgucomai.tableorder.repository;

<<<<<<< HEAD
import dgucomai.tableorder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
=======
import dgucomai.tableorder.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {}
>>>>>>> 2235135b6b6ca490eb75009ad3cd6e25b5f91218
