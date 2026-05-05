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
    private int totalAmount; // 기획서 명칭인 totalAmount로 바꿨습니다!

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 아래 정의한 상태값들을 사용합니다.

    private LocalDateTime createdAt;

    // 기획서에 정의된 7가지 상태값입니다.[cite: 1]
    public enum OrderStatus {
        CREATED,          // 주문 생성 (이체 전)[cite: 1]
        PAYMENT_PENDING,  // 입금 확인 대기[cite: 1]
        APPROVED,         // 결제 승인 완료[cite: 1]
        REJECTED,         // 결제 반려[cite: 1]
        COOKING,          // 조리 중[cite: 1]
        DONE,             // 서빙 완료[cite: 1]
        CANCELLED         // 주문 취소[cite: 1]
    }
}