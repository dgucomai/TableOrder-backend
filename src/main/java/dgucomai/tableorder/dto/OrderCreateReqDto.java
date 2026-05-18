package dgucomai.tableorder.dto;

import java.util.List;

public record OrderCreateReqDto(String qrToken, List<OrderItemReqDto> items) {

  public record OrderItemReqDto(Long menuId, int quantity) {}
}
