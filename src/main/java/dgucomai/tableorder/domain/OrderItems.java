package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
public class OrderItems {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_item_id")
  private Long orderItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Orders orders;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_id")
  private MenuItems menuItems;

  private int quantity;

  @Column(name = "unit_price")
  private int unitPrice;

  private int subtotal;

  public OrderItems(Orders orders, MenuItems menuItems, int quantity) {
    this.orders = orders;
    this.menuItems = menuItems;
    this.quantity = quantity;
    this.unitPrice = menuItems.getPrice();
    this.subtotal = menuItems.getPrice() * quantity;
  }
}
