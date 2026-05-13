package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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

  @Column(name = "call_type")
  private String callType;

  public StaffCall(Long tableId) {
    this.tableId = tableId;
    this.callTime = LocalDateTime.now();
    this.isResolved = false;
  }
}
