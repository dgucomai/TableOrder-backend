package dgucomai.tableorder.domain;

import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "table_id")
  private Long tableId;

  @Column(name = "session_id")
  private Long sessionId;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", length = 20)
  private OrderStatus orderStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", length = 20)
  private PaymentStatus paymentStatus;

  private LocalDateTime checkedAt;
  private Long checkedByStaffId;
  private String checkedByStaffName;

  @Column(name = "changed_by")
  private Long changedBy;

  // --- 정환 님 추가 필드 시작 ---

  @Column(name = "session_id")
  private Long sessionId;

  // --- 정환 님 추가 필드 끝 ---

  @Column(name = "total_amount")
  private int totalAmount;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItems> orderItems = new ArrayList<>();

  public Orders(Long tableId, Long sessionId, int totalAmount) {
    this.tableId = tableId;
    this.sessionId = sessionId;
    this.orderStatus = OrderStatus.PAYMENT_PENDING;
    this.paymentStatus = PaymentStatus.PENDING;
    this.totalAmount = totalAmount;
    this.createdAt = LocalDateTime.now();
  }

  public void addOrderItem(OrderItems orderItems) {
    this.orderItems.add(orderItems);
  }

  public void updateStatus(String status) {
    this.orderStatus = OrderStatus.valueOf(status.toUpperCase());

    if (this.orderStatus == OrderStatus.COOKING) {
      this.approvedAt = LocalDateTime.now();
      this.paymentStatus = PaymentStatus.APPROVED;
    } else if (this.orderStatus == OrderStatus.COMPLETED
        || this.orderStatus == OrderStatus.CANCELLED) {
      this.completedAt = LocalDateTime.now();
    }
  }
}
