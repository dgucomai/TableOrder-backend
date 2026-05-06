package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuName;
    private int totalAmount; // price 대신 totalAmount 사용

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    // 이 부분(OrderStatus)이 정확히 있어야 Service에서 에러가 안 납니다.[cite: 1]
    public enum OrderStatus {
        CREATED, PAYMENT_PENDING, APPROVED, REJECTED, COOKING, DONE, CANCELLED
    }
}