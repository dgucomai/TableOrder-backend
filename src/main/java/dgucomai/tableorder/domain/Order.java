package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity // 1. 이 클래스는 DB의 테이블과 연결됩니다.
@Table(name = "orders")
@Getter @Setter // 2. 데이터를 넣고 빼는 기능을 자동으로 만듭니다.
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 고유 번호

    private String menuName; // 메뉴 이름
    private Integer price;   // 가격

    // 3. 주문 상태 (WAITING, ACCEPTED, CANCEL 등)
    // 정빈 님이 '승인'을 누르면 이 상태가 변하게 됩니다.
    private String status;
}