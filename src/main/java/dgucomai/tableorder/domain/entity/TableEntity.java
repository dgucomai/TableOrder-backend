package dgucomai.tableorder.domain.entity;

import dgucomai.tableorder.domain.type.TableStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity //DB 테이블이랑 연결할 클래스
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tables") //DB에서 테이블 이름이 "tables"

public class TableEntity {

    @Id //기본 (Primary Key)
    @Column(name = "table_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId; //테이블 고유 번호

    @Column(name = "table_number")
    private Integer tableNumber; //테이블 번호 (1번 테이블, 2번 테이블, ...)

    @Column(name = "qr_token")
    private String qrToken; //QR코드용 고유 토큰

    @Column(name = "status")
    @Enumerated(EnumType.STRING) //DB에 "ACTIVE", "INACTIVE" 문자열로 저장
    private TableStatus status; //테이블 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; //생성 시각
}