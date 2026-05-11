package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_categories")
@Getter
@NoArgsConstructor
public class MenuCategories {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Long id;

  @Column(name = "category_name")
  private String categoryName;

  @Column(name = "display_order")
  private Integer displayOrder;
}
