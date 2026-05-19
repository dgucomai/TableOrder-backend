package dgucomai.tableorder.domain.response.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.type.TableStatus;

// 전체 테이블 조회할때 응답으로 돌려줄 데이터 형식
public record TableSummaryResponseDto(Long tableId, Integer tableNumber, TableStatus status) {
  // form 메서드 : TableEntity를 TableSummaryResponseDto로 변환하는 메서드
  public static TableSummaryResponseDto from(TableEntity table, TableStatus calculatedStatus) {
    return new TableSummaryResponseDto(
        table.getTableId(), table.getTableNumber(), calculatedStatus);
  }
}
