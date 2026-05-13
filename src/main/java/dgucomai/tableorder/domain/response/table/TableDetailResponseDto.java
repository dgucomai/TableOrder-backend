package dgucomai.tableorder.domain.response.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.type.TableStatus;
import java.time.LocalDateTime;

// 전체 테이블 조회할때 응답으로 돌려줄 데이터 형식
public record TableDetailResponseDto(
    Long tableId,
    Integer tableNumber,
    TableStatus status,
    Long sessionId,
    TableStatus sessionStatus,
    LocalDateTime startedAt,
    String qrToken,
    LocalDateTime createdAt) {
  // form 메서드 : TableEntity를 TableSummaryResponseDto로 변환하는 메서드
  public static TableDetailResponseDto from(TableEntity table, TableSession session) {
    return new TableDetailResponseDto(
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
