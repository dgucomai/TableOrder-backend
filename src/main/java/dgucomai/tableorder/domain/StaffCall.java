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
  @Column(name = "id")
  private Long id;

  @Column(name = "table_id", nullable = false)
  private Long tableId;

  @Column(name = "message")
  private String message;

  @Column(name = "status")
  private String status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;

  public StaffCall(Long tableId, String message) {
    this.tableId = tableId;
    this.message = message;
    this.status = "REQUESTED";
    this.createdAt = LocalDateTime.now();
  }

  public void resolve() {
    this.status = "RESOLVED";
    this.resolvedAt = LocalDateTime.now();
  }
}
