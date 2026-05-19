package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calls")
@Getter
@NoArgsConstructor
public class StaffCall {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "call_id")
  private Long callId;

  @Column(name = "table_id", nullable = false)
  private Long tableId;

  @Column(name = "session_id")
  private Long sessionId;

  @Column(name = "call_type")
  private String callType;

  @Column(name = "message")
  private String message;

  @Column(name = "status")
  private String status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;

  @Column(name = "resolved_by")
  private Long resolvedBy;

  public StaffCall(Long tableId, Long sessionId, String callType, String message) {
    this.tableId = tableId;
    this.sessionId = sessionId;
    this.callType = callType;
    this.message = message;
    this.status = "REQUESTED";
    this.createdAt = LocalDateTime.now();
  }

  public void resolve(Long staffId) {
    this.status = "RESOLVED";
    this.resolvedAt = LocalDateTime.now();
    this.resolvedBy = staffId;
  }
}
