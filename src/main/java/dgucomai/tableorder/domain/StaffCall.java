package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "staff_calls")
public class StaffCall {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long tableId;

  @Column(nullable = false)
  private LocalDateTime callTime;

  private boolean isResolved;

  public StaffCall(Long tableId) {
    this.tableId = tableId;
    this.callTime = LocalDateTime.now();
    this.isResolved = false;
  }
}
