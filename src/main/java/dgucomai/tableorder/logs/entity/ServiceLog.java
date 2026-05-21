package dgucomai.tableorder.logs.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_logs")
@Getter
@NoArgsConstructor
public class ServiceLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id")
  private Long logId;

  @Column(name = "actor_type")
  private String actorType;

  @Column(name = "message", columnDefinition = "TEXT")
  private String message;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public ServiceLog(String actorType, String message) {
    this.actorType = actorType;
    this.message = message;
    this.createdAt = LocalDateTime.now();
  }
}
