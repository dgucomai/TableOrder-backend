package dgucomai.tableorder.domain;

import dgucomai.tableorder.domain.enums.OrderStatus;
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

  @Column(name = "session_id")
  private Long sessionId;

  @Column(name = "table_id")
  private Long tableId;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", length = 20)
  private OrderStatus orderStatus;

  @Column(name = "changed_by")
  private Long changedBy;

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

  public Orders(Long sessionId, Long tableId, int totalAmount) {
    this.sessionId = sessionId;
    this.tableId = tableId;
    this.orderStatus = OrderStatus.PAYMENT_PENDING;
    this.totalAmount = totalAmount;
    this.createdAt = LocalDateTime.now();
  }

  public void addOrderItem(OrderItems orderItems) {
    this.orderItems.add(orderItems);
  }

  public void updateStatus(OrderStatus status, Long staffId) {
    this.orderStatus = status;
    this.changedBy = staffId;

    if (this.orderStatus == OrderStatus.COOKING) {
      this.approvedAt = LocalDateTime.now();
    } else if (this.orderStatus == OrderStatus.COMPLETED) {
      this.completedAt = LocalDateTime.now();
    }
  }
}
