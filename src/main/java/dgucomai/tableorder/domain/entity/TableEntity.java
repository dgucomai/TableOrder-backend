package dgucomai.tableorder.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tables")
public class TableEntity {

  @Id
  @Column(name = "table_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tableId;

  @Column(name = "table_number")
  private Integer tableNumber;

  @Column(name = "qr_token")
  private String qrToken;

  @Column(name = "current_session_id")
  private Long currentSessionId;
}
