package dgucomai.tableorder.dto;

public record TokenUpdateResDto(
    Long tableId,
    Integer tableNumber,
    Integer previousTokenCount,
    Integer delta,
    Integer currentTokenCount) {}
