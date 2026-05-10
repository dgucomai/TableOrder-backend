package dgucomai.tableorder.dto;

import java.util.List;

public record OrderCreateReqDto(Long tableId, List<OrderItemReqDto> items) {

  public record OrderItemReqDto(Long menuId, int quantity) {}
}
