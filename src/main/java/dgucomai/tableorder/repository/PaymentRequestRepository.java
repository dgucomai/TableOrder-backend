package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.entity.PaymentRequest;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

  @Query(
      "SELECT COALESCE(SUM(pr.order.totalAmount), 0) FROM PaymentRequest pr WHERE pr.paymentStatus = :status")
  Long sumTotalAmountByStatus(@Param("status") PaymentStatus status);
}
