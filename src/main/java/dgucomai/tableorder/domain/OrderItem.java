package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuName;
    private int quantity;
    private int price;

    public OrderItem(String menuName, int quantity, int price) {
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }
}