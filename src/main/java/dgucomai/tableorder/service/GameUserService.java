package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.GameUsers;
import dgucomai.tableorder.domain.TableSessions;
import dgucomai.tableorder.domain.Tables;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.dto.GameRankingItemResDto;
import dgucomai.tableorder.dto.GameRankingListResDto;
import dgucomai.tableorder.dto.GameUserCreateReqDto;
import dgucomai.tableorder.dto.GameUserCreateResDto;
import dgucomai.tableorder.dto.GameUserResDto;
import dgucomai.tableorder.dto.GameUserUpdateReqDto;
import dgucomai.tableorder.dto.GameUserUpdateResDto;
import dgucomai.tableorder.exception.GameApiException;
import dgucomai.tableorder.repository.GameUserRepository;
import dgucomai.tableorder.repository.GameTableRepository;
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
    private final GameTableRepository tableRepository;

    @Transactional
    public GameUserCreateResDto createUser(GameUserCreateReqDto request) {
        validateCreateRequest(request);

        Tables table =
                tableRepository
                        .findByQrToken(request.qrToken())
                        .orElseThrow(() -> new GameApiException(HttpStatus.NOT_FOUND, "QR 토큰에 해당하는 테이블을 찾을 수 없습니다."));

        TableSessions currentSession = table.getCurrentSession();

        if (currentSession == null || currentSession.getStatus() != TableSessionStatus.ACTIVE) {
            throw new GameApiException(HttpStatus.NOT_FOUND, "현재 활성화된 테이블 세션을 찾을 수 없습니다.");
        }

        GameUsers gameUsers =
                new GameUsers(currentSession.getSessionId(), request.nickname(), request.phoneNumber());

        GameUsers savedUser = gameUserRepository.save(gameUsers);

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
                request.score_1(),
                request.score_2(),
                request.score_3(),
                request.score_4());

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

        return new GameRankingListResDto("game" + game, rankings);
    }

    private List<GameUsers> findTop5ByGameIndex(int game) {
        return switch (game) {
            case 1 -> gameUserRepository.findTop5ByOrderByScore1DescCreatedAtAsc();
            case 2 -> gameUserRepository.findTop5ByOrderByScore2DescCreatedAtAsc();
            case 3 -> gameUserRepository.findTop5ByOrderByScore3DescCreatedAtAsc();
            case 4 -> gameUserRepository.findTop5ByOrderByScore4DescCreatedAtAsc();
            default -> throw new GameApiException(HttpStatus.BAD_REQUEST, "지원하지 않는 게임 인덱스입니다.");
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

    private void validateGameIndex(Integer game) {
        if (game == null) {
            throw new GameApiException(HttpStatus.BAD_REQUEST, "게임 인덱스는 필수입니다.");
        }

        if (game < 1 || game > 4) {
            throw new GameApiException(HttpStatus.BAD_REQUEST, "지원하지 않는 게임 인덱스입니다.");
        }
    }
}