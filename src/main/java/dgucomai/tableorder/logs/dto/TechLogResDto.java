package dgucomai.tableorder.logs.dto;

import dgucomai.tableorder.logs.entity.TechLog;
import java.time.LocalDateTime;

public record TechLogResDto(
    Long logId,
    String method,
    String uri,
    String requestBody,
    Integer responseStatus,
    Long durationMs,
    String ipAddress,
    LocalDateTime createdAt) {

  public static TechLogResDto from(TechLog log) {
    return new TechLogResDto(
        log.getLogId(),
        log.getMethod(),
        log.getUri(),
        log.getRequestBody(),
        log.getResponseStatus(),
        log.getDurationMs(),
        log.getIpAddress(),
        log.getCreatedAt());
  }
}
