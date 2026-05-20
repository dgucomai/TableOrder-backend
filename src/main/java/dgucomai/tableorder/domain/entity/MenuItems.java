package dgucomai.tableorder.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_items")
@Getter
@NoArgsConstructor
public class MenuItems {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "menu_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private MenuCategories category;

  @Column(name = "menu_name")
  private String menuName;

  private int price;
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "is_sold_out")
  private boolean isSoldOut;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public void updateSoldOut(boolean isSoldOut) {
    this.isSoldOut = isSoldOut;
  }
}
