package dgucomai.tableorder.domain.entity;

import dgucomai.tableorder.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_requests")
@Getter
@Setter
@NoArgsConstructor
public class PaymentRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long paymentId;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private Orders order;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", length = 20)
  private PaymentStatus paymentStatus;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  public PaymentRequest(Long orderId) {
    this.orderId = orderId;
    this.paymentStatus = PaymentStatus.PENDING;
    this.requestedAt = LocalDateTime.now();
  }

  public void approve() {
    this.paymentStatus = PaymentStatus.APPROVED;
  }
}
