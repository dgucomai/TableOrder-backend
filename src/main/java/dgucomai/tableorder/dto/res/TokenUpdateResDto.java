package dgucomai.tableorder.dto.res;

public record TokenUpdateResDto(
    Long tableId,
    Integer tableNumber,
    Integer previousTokenCount,
    Integer delta,
    Integer currentTokenCount) {}
