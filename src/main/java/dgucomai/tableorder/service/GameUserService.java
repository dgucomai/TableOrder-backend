package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.entity.GameUsers;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.dto.req.GameUserCreateReqDto;
import dgucomai.tableorder.dto.req.GameUserUpdateReqDto;
import dgucomai.tableorder.dto.res.GameRankingItemResDto;
import dgucomai.tableorder.dto.res.GameRankingListResDto;
import dgucomai.tableorder.dto.res.GameUserCreateResDto;
import dgucomai.tableorder.dto.res.GameUserResDto;
import dgucomai.tableorder.dto.res.GameUserUpdateResDto;
import dgucomai.tableorder.exception.GameApiException;
import dgucomai.tableorder.logs.service.LogService;
import dgucomai.tableorder.repository.GameUserRepository;
import dgucomai.tableorder.repository.table.TableRepository;
import dgucomai.tableorder.repository.table.TableSessionRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameUserService {

  private final GameUserRepository gameUserRepository;
  private final TableRepository tableRepository;
  private final TableSessionRepository tableSessionRepository;
  private final LogService logService;

  @Transactional
  public GameUserCreateResDto createUser(GameUserCreateReqDto request) {
    validateCreateRequest(request);

    Tables table =
        tableRepository
            .findByQrToken(request.qrToken())
            .orElseThrow(
                () -> new GameApiException(HttpStatus.NOT_FOUND, "QR 토큰에 해당하는 테이블을 찾을 수 없습니다."));

    TableSession currentSession = null;
    if (table.getCurrentSessionId() != null) {
      currentSession = tableSessionRepository.findById(table.getCurrentSessionId()).orElse(null);
    }

    if (currentSession == null || currentSession.getStatus() != TableSessionStatus.ACTIVE) {
      throw new GameApiException(HttpStatus.NOT_FOUND, "현재 활성화된 테이블 세션을 찾을 수 없습니다.");
    }

    GameUsers gameUsers =
        new GameUsers(currentSession.getSessionId(), request.nickname(), request.phoneNumber());

    GameUsers savedUser = gameUserRepository.save(gameUsers);

    String phoneLast4 = getPhoneLast4(savedUser.getPhoneNumber());
    logService.saveServiceLog(
        "GAME",
        table.getTableNumber()
            + "번 테이블에서 게임 사용자 "
            + savedUser.getNickname()
            + "("
            + phoneLast4
            + ")이 등록되었습니다.");

    return GameUserCreateResDto.from(savedUser, table);
  }

  @Transactional
  public GameUserUpdateResDto updateUser(Long userId, GameUserUpdateReqDto request) {
    validateUpdateRequest(request);

    GameUsers gameUsers =
        gameUserRepository
            .findById(userId)
            .orElseThrow(() -> new GameApiException(HttpStatus.NOT_FOUND, "해당 게임 사용자를 찾을 수 없습니다."));

    gameUsers.update(
        request.nickname(),
        request.phoneNumber(),
        request.flappyBirdScore(),
        request.rockPaperScissorsScore(),
        request.akoGrowingScore(),
        request.basketballScore());

    String phoneLast4 = getPhoneLast4(gameUsers.getPhoneNumber());
    logService.saveServiceLog(
        "GAME",
        "게임 사용자 "
            + gameUsers.getNickname()
            + "("
            + phoneLast4
            + ")(userId "
            + userId
            + "번) 정보가 수정되었습니다.");

    return GameUserUpdateResDto.from(gameUsers);
  }

  public GameUserResDto getUser(Long userId) {
    GameUsers gameUsers =
        gameUserRepository
            .findById(userId)
            .orElseThrow(() -> new GameApiException(HttpStatus.NOT_FOUND, "해당 게임 사용자를 찾을 수 없습니다."));

    return GameUserResDto.from(gameUsers);
  }

  public GameRankingListResDto getRanking(Integer game) {
    validateGameIndex(game);

    List<GameUsers> gameUsers = findTop5ByGameIndex(game);
    List<GameRankingItemResDto> rankings = new ArrayList<>();

    for (int i = 0; i < gameUsers.size(); i++) {
      rankings.add(GameRankingItemResDto.from(gameUsers.get(i), i + 1, game));
    }

    return new GameRankingListResDto(getGameName(game), rankings);
  }

  private TableSession getActiveSession(Tables table) {
    Long currentSessionId = table.getCurrentSessionId();

    if (currentSessionId == null) {
      throw new GameApiException(HttpStatus.NOT_FOUND, "현재 활성화된 테이블 세션을 찾을 수 없습니다.");
    }

    TableSession currentSession =
        tableSessionRepository
            .findById(currentSessionId)
            .orElseThrow(
                () -> new GameApiException(HttpStatus.NOT_FOUND, "현재 활성화된 테이블 세션을 찾을 수 없습니다."));

    if (currentSession.getStatus() != TableSessionStatus.ACTIVE) {
      throw new GameApiException(HttpStatus.NOT_FOUND, "현재 활성화된 테이블 세션을 찾을 수 없습니다.");
    }

    return currentSession;
  }

  private List<GameUsers> findTop5ByGameIndex(int game) {
    return switch (game) {
      case 1 -> gameUserRepository.findTop5ByOrderByFlappyBirdScoreDescCreatedAtAsc();
      case 2 -> gameUserRepository.findTop5ByOrderByRockPaperScissorsScoreDescCreatedAtAsc();
      case 3 -> gameUserRepository.findTop5ByOrderByAkoGrowingScoreDescCreatedAtAsc();
      case 4 -> gameUserRepository.findTop5ByOrderByBasketballScoreDescCreatedAtAsc();
      default -> throw new GameApiException(HttpStatus.BAD_REQUEST, "지원하지 않는 게임 인덱스입니다.");
    };
  }

  private String getGameName(int game) {
    return switch (game) {
      case 1 -> "flappyBird";
      case 2 -> "rockPaperScissors";
      case 3 -> "akoGrowing";
      case 4 -> "basketball";
      default -> "unknown";
    };
  }

  private void validateCreateRequest(GameUserCreateReqDto request) {
    if (request == null
        || request.qrToken() == null
        || request.qrToken().isBlank()
        || request.nickname() == null
        || request.nickname().isBlank()
        || request.phoneNumber() == null
        || request.phoneNumber().isBlank()) {
      throw new GameApiException(HttpStatus.BAD_REQUEST, "게임 사용자 등록 요청 형식이 올바르지 않습니다.");
    }

    validatePhoneNumber(request.phoneNumber());
  }

  private void validateUpdateRequest(GameUserUpdateReqDto request) {
    if (request == null || request.hasNoUpdateFields()) {
      throw new GameApiException(HttpStatus.BAD_REQUEST, "게임 사용자 수정 요청 형식이 올바르지 않습니다.");
    }

    if (request.nickname() != null && request.nickname().isBlank()) {
      throw new GameApiException(HttpStatus.BAD_REQUEST, "게임 사용자 수정 요청 형식이 올바르지 않습니다.");
    }

    if (request.phoneNumber() != null) {
      validatePhoneNumber(request.phoneNumber());
    }
  }

  private void validatePhoneNumber(String phoneNumber) {
    if (!phoneNumber.matches("^[0-9]+$")) {
      throw new GameApiException(HttpStatus.BAD_REQUEST, "전화번호 형식이 올바르지 않습니다.");
    }
  }

  private String getPhoneLast4(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.length() <= 4) return phoneNumber;
    return phoneNumber.substring(phoneNumber.length() - 4);
  }

  private void validateGameIndex(Integer game) {
    if (game == null) {
      throw new GameApiException(HttpStatus.BAD_REQUEST, "게임 인덱스는 필수입니다.");
    }

    if (game < 1 || game > 4) {
      throw new GameApiException(HttpStatus.BAD_REQUEST, "지원하지 않는 게임 인덱스입니다.");
    }
  }
}
