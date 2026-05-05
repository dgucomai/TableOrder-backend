package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "staff_calls")
public class StaffCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tableId;
    private LocalDateTime callTime;
    private boolean isResolved;

    public StaffCall(Long tableId) {
        this.tableId = tableId;
        this.callTime = LocalDateTime.now();
        this.isResolved = false;
    }
}