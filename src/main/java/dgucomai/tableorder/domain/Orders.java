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
@Getter @Setter
@NoArgsConstructor
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "table_id")
  private Long tableId;

  @Enumerated(EnumType.STRING)
  @Column(length = 20,name = "order_status")
  private OrderStatus orderStatus;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private PaymentStatus paymentStatus;

  private LocalDateTime checkedAt;
  private Long checkedByStaffId;
  private String checkedByStaffName;

  private int totalAmount;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItems> orderItems = new ArrayList<>();

  public Orders(Long tableId, int totalAmount) {
    this.tableId = tableId;
    this.totalAmount = totalAmount;
    this.createdAt = LocalDateTime.now();
  }

  public void addOrderItem(OrderItems orderItems) {
    this.orderItems.add(orderItems);
  }
  public enum PaymentStatus {
    PENDING, APPROVED, REFUNDED, REJECTED
  }

  public enum OrderStatus {
    CREATED, COOKING, COMPLETED, CANCELLED, REJECTED
  }

  public void updateStatus(String status) {
    this.orderStatus = OrderStatus.valueOf(status);

    if (this.orderStatus == OrderStatus.COOKING) {
      this.approvedAt = LocalDateTime.now();
    } else if (this.orderStatus == OrderStatus.COMPLETED) {
      this.completedAt = LocalDateTime.now();
    }
  }
}
