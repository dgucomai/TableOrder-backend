package dgucomai.tableorder.domain.entity;

import dgucomai.tableorder.domain.enums.TableSessionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "table_sessions")
public class TableSession {

  @Id
  @Column(name = "session_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sessionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "table_id")
  private Tables table;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cleared_by")
  private Staff clearedBy;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private TableSessionStatus status;

  @Column(name = "token_count")
  private Integer tokenCount;

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "cleared_at")
  private LocalDateTime clearedAt;
}
