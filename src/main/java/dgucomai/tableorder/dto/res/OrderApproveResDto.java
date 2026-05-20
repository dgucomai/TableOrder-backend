package dgucomai.tableorder.dto.res;

public record OrderApproveResDto(
    Long orderId,
    String orderStatus,
    String paymentStatus,
    String checkedAt,
    Long checkedByStaffId,
    String checkedByStaffName) {}
