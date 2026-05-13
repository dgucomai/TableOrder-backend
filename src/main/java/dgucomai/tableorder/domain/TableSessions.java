package dgucomai.tableorder.domain;

import dgucomai.tableorder.domain.enums.TableSessionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_sessions")
@Getter
@NoArgsConstructor
public class TableSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "cleared_by")
    private Long clearedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableSessionStatus status;

    @Column(name = "token_count", nullable = false)
    private int tokenCount;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "cleared_at")
    private LocalDateTime clearedAt;
}