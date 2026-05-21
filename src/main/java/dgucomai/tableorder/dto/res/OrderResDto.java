package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResDto(
    Long orderId,
    Long tableId,
    String orderStatus,
    int amount,
    LocalDateTime createdAt,
    List<OrderItemResDto> items) {

  public record OrderItemResDto(
      Long orderItemId, Long menuId, String menuName, int quantity, int unitPrice, int subtotal) {}

  public static OrderResDto from(Orders orders) {
    List<OrderItemResDto> items =
        orders.getOrderItems().stream()
            .map(
                oi ->
                    new OrderItemResDto(
                        oi.getOrderItemId(),
                        oi.getMenuItems().getId(),
                        oi.getMenuItems().getMenuName(),
                        oi.getQuantity(),
                        oi.getUnitPrice(),
                        oi.getSubtotal()))
            .toList();
    return new OrderResDto(
        orders.getOrderId(),
        orders.getTableId(),
        orders.getOrderStatus().name(),
        orders.getAmount(),
        orders.getCreatedAt(),
        items);
  }
}
