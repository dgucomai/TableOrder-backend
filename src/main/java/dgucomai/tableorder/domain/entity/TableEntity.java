package dgucomai.tableorder.domain.entity;

import dgucomai.tableorder.domain.type.TableStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tables")

public class TableEntity {

    @Id
    @Column(name = "table_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;

    @Column(name = "table_number")
    private Integer tableNumber;

    @Column(name = "qr_token")
    private String qrToken;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TableStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}