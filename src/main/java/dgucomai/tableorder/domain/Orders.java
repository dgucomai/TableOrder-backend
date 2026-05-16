package dgucomai.tableorder.domain;

import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.domain.enums.PaymentStatus; // 정빈 님 추가 필드용
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter @Setter // 서비스 레이어에서 편하게 쓰기 위해 Setter 유지
@NoArgsConstructor
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "table_id")
  private Long tableId;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", length = 20)
  private OrderStatus orderStatus;

  // --- 정빈 님 추가 필드 시작 ---
  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", length = 20)
  private PaymentStatus paymentStatus;

  private LocalDateTime checkedAt;
  private Long checkedByStaffId;
  private String checkedByStaffName;
  // --- 정빈 님 추가 필드 끝 ---

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

  // [dev 유지] 기존 생성자 구조 유지 (PaymentStatus 기본값 설정 추가)
  public Orders(Long tableId, int totalAmount) {
    this.tableId = tableId;
    this.orderStatus = OrderStatus.PAYMENT_PENDING; // dev 기준값
    this.paymentStatus = PaymentStatus.PENDING;     // 정빈 님 필드 초기화
    this.totalAmount = totalAmount;
    this.createdAt = LocalDateTime.now();
  }

  public void addOrderItem(OrderItems orderItems) {
    this.orderItems.add(orderItems);
  }

  // [dev 유지] 기존 비즈니스 로직 유지 + 정빈 님 필드 업데이트 로직 추가
  public void updateStatus(String status) {
    this.orderStatus = OrderStatus.valueOf(status.toUpperCase());

    if (this.orderStatus == OrderStatus.COOKING) {
      this.approvedAt = LocalDateTime.now();
      this.paymentStatus = PaymentStatus.APPROVED; // 승인 시 결제 상태도 변경
    } else if (this.orderStatus == OrderStatus.COMPLETED) {
      this.completedAt = LocalDateTime.now();
    }
  }
}