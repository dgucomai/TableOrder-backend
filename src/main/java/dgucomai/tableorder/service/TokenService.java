package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.dto.TokenResDto;
import dgucomai.tableorder.dto.TokenUpdateResDto;
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

  // [GET] 토큰 수 조회 (status 검사 완벽 제거됨)
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

    int currentToken = session.getTokenCount() != null ? session.getTokenCount() : 0;

    return new TokenResDto(table.getTableId(), table.getTableNumber(), currentToken);
  }

  // [PATCH] 토큰 수 증감 수정 (신규 추가)
  @Transactional
  public TokenUpdateResDto updateTokenCount(Long tableId, Integer delta) {
    if (delta == null) {
      throw new CustomException(ErrorCode.INVALID_TOKEN_DELTA);
    }

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

    int previousTokenCount = session.getTokenCount() != null ? session.getTokenCount() : 0;
    int currentTokenCount = previousTokenCount + delta;

    if (currentTokenCount < 0) {
      throw new CustomException(ErrorCode.TOKEN_COUNT_NEGATIVE_NOT_ALLOWED);
    }

    session.setTokenCount(currentTokenCount);

    return new TokenUpdateResDto(
        table.getTableId(), table.getTableNumber(), previousTokenCount, delta, currentTokenCount);
  }
}
