package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.Tables;

public record TableNumResDto(Long tableId, Integer tableNumber) {

  public static TableNumResDto from(Tables table) {
    return new TableNumResDto(table.getTableId(), table.getTableNumber());
  }
}
