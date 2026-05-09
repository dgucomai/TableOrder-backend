package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id") // 👈 재호 님의 실제 DB 컬럼 이름과 연결해주는 마법의 코드입니다!
    private Long id;

    @Column(nullable = false)
    private String status;
}