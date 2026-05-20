package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.domain.type.TableStatus;

public record TableSummaryResDto(Long tableId, Integer tableNumber, TableStatus status) {
  public static TableSummaryResDto from(Tables table, TableStatus calculatedStatus) {
    return new TableSummaryResDto(table.getTableId(), table.getTableNumber(), calculatedStatus);
  }
}
