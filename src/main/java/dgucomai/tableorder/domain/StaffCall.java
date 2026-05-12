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
  private Long id;

  private Long tableId;
  private String message;
  private String status;
  private LocalDateTime createdAt;

  public StaffCall(Long tableId, String message) {
    this.tableId = tableId;
    this.message = message;
    this.status = "REQUESTED";
    this.createdAt = LocalDateTime.now();
  }
}
