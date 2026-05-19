package dgucomai.tableorder.domain.response.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.domain.type.TableStatus;
import dgucomai.tableorder.dto.OrderResDto;
import java.time.LocalDateTime;
import java.util.List;

// 전체 테이블 조회할때 응답으로 돌려줄 데이터 형식
public record TableDetailResponseDto(
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
  // form 메서드 : TableEntity를 TableSummaryResponseDto로 변환하는 메서드
  public static TableDetailResponseDto of(
      TableEntity table,
      TableSession session,
      TableStatus calculatedStatus,
      int totalAmount,
      List<OrderResDto> orders,
      List<StaffCallResDto> calls) {
    return new TableDetailResponseDto(
        table.getTableId(),
        table.getTableNumber(),
        session != null ? session.getSessionId() : null,
        calculatedStatus,
        session != null ? session.getStatus() : TableSessionStatus.CLOSED,
        totalAmount,
        session != null ? session.getTokenCount() : 0,
        session != null ? session.getStartedAt() : null,
        orders,
        calls);
  }

  public static TableDetailResponseDto empty(TableEntity table, Long sessionId) {
    return new TableDetailResponseDto(
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
