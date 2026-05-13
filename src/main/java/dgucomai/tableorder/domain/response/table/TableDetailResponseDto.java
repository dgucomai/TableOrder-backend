package dgucomai.tableorder.domain.response.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.type.TableStatus;
import java.time.LocalDateTime;

// 전체 테이블 조회할때 응답으로 돌려줄 데이터 형식
public record TableDetailResponseDto(
    Long tableId,
    Integer tableNumber,
    TableStatus status,
    String qrToken,
    LocalDateTime createdAt) {
  // form 메서드 : TableEntity를 TableSummaryResponseDto로 변환하는 메서드
  public static TableDetailResponseDto from(TableEntity table) {
    return new TableDetailResponseDto(
        table.getTableId(),
        table.getTableNumber(),
        table.getStatus(),
        table.getQrToken(),
        table.getCreatedAt());
  }
}
