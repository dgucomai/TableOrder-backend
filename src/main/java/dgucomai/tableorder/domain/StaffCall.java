package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity // 이 클래스가 DB의 테이블과 1:1로 매칭됨을 의미합니다.
@Getter // 각 필드(데이터)를 외부에서 읽을 수 있게 해줍니다.
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동으로 만들어줍니다 (JPA 필수사항).
@Table(name = "staff_calls") // DB에 저장될 실제 테이블 이름을 지정합니다.
public class StaffCall {

    @Id // 이 필드가 테이블의 고유 번호(Primary Key)가 됩니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호를 1, 2, 3... 순서대로 자동 생성합니다.
    private Long id;

    @Column(nullable = false) // 테이블 번호는 반드시 있어야 하므로 null을 허용하지 않습니다.
    private Long tableId;

    @Column(nullable = false)
    private LocalDateTime callTime; // 호출이 발생한 정확한 시간입니다.

    private boolean isResolved; // 직원이 호출을 확인하고 해결했는지 여부 (true/false)

    // 새로운 호출이 들어왔을 때 사용할 생성자입니다.
    public StaffCall(Long tableId) {
        this.tableId = tableId;
        this.callTime = LocalDateTime.now(); // 생성되는 순간의 현재 시간을 저장합니다.
        this.isResolved = false; // 처음엔 당연히 미해결 상태이므로 false를 넣습니다.
    }
}