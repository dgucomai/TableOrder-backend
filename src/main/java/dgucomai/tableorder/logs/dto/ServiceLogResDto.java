package dgucomai.tableorder.logs.dto;

import dgucomai.tableorder.logs.entity.ServiceLog;
import java.time.LocalDateTime;

public record ServiceLogResDto(
    Long logId, String actorType, String message, LocalDateTime createdAt) {

  public static ServiceLogResDto from(ServiceLog log) {
    return new ServiceLogResDto(
        log.getLogId(), log.getActorType(), log.getMessage(), log.getCreatedAt());
  }
}
