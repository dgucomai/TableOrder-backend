package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.domain.type.TableStatus;
import java.time.LocalDateTime;
import java.util.List;

public record TableDetailResDto(
    Long tableId,
    Integer tableNumber,
    Long sessionId,
    TableStatus tableStatus,
    TableSessionStatus sessionStatus,
    Integer tokenAmount,
    Integer tokenCount,
    LocalDateTime startedAt,
    List<OrderResDto> orders,
    List<StaffCallResDto> calls) {
  public static TableDetailResDto of(
      Tables table,
      TableSession session,
      TableStatus calculatedStatus,
      int amount,
      List<OrderResDto> orders,
      List<StaffCallResDto> calls) {
    return new TableDetailResDto(
        table.getTableId(),
        table.getTableNumber(),
        session != null ? session.getSessionId() : null,
        calculatedStatus,
        session != null ? session.getStatus() : TableSessionStatus.CLOSED,
        amount,
        session != null ? session.getTokenCount() : 0,
        session != null ? session.getStartedAt() : null,
        orders,
        calls);
  }

  public static TableDetailResDto empty(Tables table, Long sessionId) {
    return new TableDetailResDto(
        table.getTableId(),
        table.getTableNumber(),
        sessionId,
        TableStatus.EMPTY,
        TableSessionStatus.CLOSED,
        0,
        0,
        null,
        List.of(),
        List.of());
  }

  public record StaffCallResDto(
      Long callId, String callType, String message, String status, LocalDateTime createdAt) {}
}
