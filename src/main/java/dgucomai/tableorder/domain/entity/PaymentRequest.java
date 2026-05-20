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
  @Column(name = "payment_request_id")
  private Long id;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", insertable = false, updatable = false)
  private Orders order;

  @Column(name = "amount", nullable = false)
  private int amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", length = 20)
  private PaymentStatus paymentStatus;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  @Column(name = "checked_at")
  private LocalDateTime checkedAt;

  @Column(name = "checked_by")
  private Long checkedBy;

  @Column(name = "session_id", nullable = false)
  private Long sessionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", insertable = false, updatable = false)
  private TableSession tableSession;

  public PaymentRequest(Long orderId, Long sessionId, int amount) {
    this.orderId = orderId;
    this.sessionId = sessionId;
    this.amount = amount;
    this.paymentStatus = PaymentStatus.PENDING;
    this.requestedAt = LocalDateTime.now();
  }

  public void approve(Long staffId) {
    this.paymentStatus = PaymentStatus.APPROVED;
    this.checkedAt = LocalDateTime.now();
    this.checkedBy = staffId;
  }

  public void reject(Long staffId) {
    this.paymentStatus = PaymentStatus.REJECTED;
    this.checkedAt = LocalDateTime.now();
    this.checkedBy = staffId;
  }
}
