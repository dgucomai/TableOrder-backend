package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tables")
@Getter
@NoArgsConstructor
public class Tables {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "table_id")
  private Long tableId;

  @Column(name = "table_number", nullable = false)
  private Integer tableNumber;

  @Column(name = "qr_token", nullable = false)
  private String qrToken;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "current_session_id")
  private TableSessions currentSession;
}
