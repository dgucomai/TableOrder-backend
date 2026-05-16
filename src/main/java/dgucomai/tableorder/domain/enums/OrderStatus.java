package dgucomai.tableorder.domain.enums;

public enum OrderStatus {
  PAYMENT_PENDING,
  REJECTED,
  APPROVED,
  COMPLETED,
  COOKING
}

public enum PaymentStatus {
  PENDING, APPROVED, REFUNDED, REJECTED
}