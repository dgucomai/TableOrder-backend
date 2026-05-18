package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.type.TableStatus;
import dgucomai.tableorder.dto.TokenResDto;
import dgucomai.tableorder.exception.CustomException;
import dgucomai.tableorder.exception.ErrorCode;
import dgucomai.tableorder.repository.table.TableRepository;
import dgucomai.tableorder.repository.table.TableSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

  private final TableRepository tableRepository;
  private final TableSessionRepository tableSessionRepository;

  public TokenResDto getTokenCount(Long tableId) {
    TableEntity table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new CustomException(ErrorCode.TABLE_NOT_FOUND));

    Long sessionId = table.getCurrentSessionId();
    if (sessionId == null) {
      throw new CustomException(ErrorCode.TABLE_SESSION_NOT_FOUND);
    }

    TableSession session =
        tableSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new CustomException(ErrorCode.TABLE_SESSION_NOT_FOUND));

    if (session.getStatus() != TableStatus.ACTIVE) {
      throw new CustomException(ErrorCode.TABLE_SESSION_NOT_FOUND);
    }

    int currentToken = session.getTokenCount() != null ? session.getTokenCount() : 0;

    return new TokenResDto(table.getTableId(), table.getTableNumber(), currentToken);
  }
}
