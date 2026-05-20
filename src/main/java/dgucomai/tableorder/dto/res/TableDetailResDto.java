package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.domain.type.TableStatus;
import java.time.LocalDateTime;

public record TableDetailResDto(
    Long tableId,
    Integer tableNumber,
    TableStatus status,
    Long sessionId,
    TableStatus sessionStatus,
    LocalDateTime startedAt,
    String qrToken,
    LocalDateTime createdAt) {

  public static TableDetailResDto from(Tables table, TableSession session) {
    return new TableDetailResDto(
        table.getTableId(),
        table.getTableNumber(),
        table.getStatus(),
        session != null ? session.getSessionId() : null,
        session != null ? session.getStatus() : null,
        session != null ? session.getStartedAt() : null,
        table.getQrToken(),
        table.getCreatedAt());
  }
}
