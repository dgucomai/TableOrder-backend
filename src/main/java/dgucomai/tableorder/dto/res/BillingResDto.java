package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;

public record BillingResDto(List<BillingOrderResDto> orders) {

  public record BillingOrderResDto(
      Long orderId, LocalDateTime createdAt, String orderStatus, List<BillingItemResDto> items) {

    public record BillingItemResDto(Long orderItemId, int quantity, int subtotal) {}

    public static BillingOrderResDto from(Orders order) {
      List<BillingItemResDto> items =
          order.getOrderItems().stream()
              .map(
                  oi ->
                      new BillingItemResDto(
                          oi.getOrderItemId(), oi.getQuantity(), oi.getSubtotal()))
              .toList();
      return new BillingOrderResDto(
          order.getOrderId(), order.getCreatedAt(), order.getOrderStatus().name(), items);
    }
  }

  public static BillingResDto from(List<Orders> orders) {
    return new BillingResDto(orders.stream().map(BillingOrderResDto::from).toList());
  }
}
