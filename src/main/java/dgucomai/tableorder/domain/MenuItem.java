package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Getter
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    // 카테고리 테이블이랑 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MenuCategory category;

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
}