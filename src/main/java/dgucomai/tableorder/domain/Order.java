package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tableId;
    private LocalDateTime orderTime;
    private String status; // WAITING, ACCEPTED, CANCELLED

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Long tableId) {
        this.tableId = tableId;
        this.orderTime = LocalDateTime.now();
        this.status = "WAITING";
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }
}