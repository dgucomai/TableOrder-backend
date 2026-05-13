package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  private Long tableId;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private OrderStatus orderStatus;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private PaymentStatus paymentStatus;

  private LocalDateTime checkedAt;
  private Long checkedByStaffId;
  private String checkedByStaffName;

  private LocalDateTime completedAt;

  private int totalAmount;
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "orders")
  private List<OrderItems> orderItems = new ArrayList<>();

  public enum OrderStatus {
    CREATED, COOKING, COMPLETED, CANCELLED, REJECTED
  }

  public enum PaymentStatus {
    PENDING, APPROVED, REFUNDED, REJECTED
  }
}