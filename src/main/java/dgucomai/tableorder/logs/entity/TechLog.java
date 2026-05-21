package dgucomai.tableorder.logs.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tech_logs")
@Getter
@NoArgsConstructor
public class TechLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id")
  private Long logId;

  @Column(name = "method")
  private String method;

  @Column(name = "uri")
  private String uri;

  @Column(name = "request_body", columnDefinition = "TEXT")
  private String requestBody;

  @Column(name = "response_status")
  private Integer responseStatus;

  @Column(name = "duration_ms")
  private Long durationMs;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public TechLog(
      String method,
      String uri,
      String requestBody,
      Integer responseStatus,
      Long durationMs,
      String ipAddress) {
    this.method = method;
    this.uri = uri;
    this.requestBody = requestBody;
    this.responseStatus = responseStatus;
    this.durationMs = durationMs;
    this.ipAddress = ipAddress;
    this.createdAt = LocalDateTime.now();
  }
}
