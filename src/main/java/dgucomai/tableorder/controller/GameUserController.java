package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.GameRankingListResDto;
import dgucomai.tableorder.dto.GameUserCreateReqDto;
import dgucomai.tableorder.dto.GameUserCreateResDto;
import dgucomai.tableorder.dto.GameUserResDto;
import dgucomai.tableorder.dto.GameUserUpdateReqDto;
import dgucomai.tableorder.dto.GameUserUpdateResDto;
import dgucomai.tableorder.service.GameUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GameUserController {

    private final GameUserService gameUserService;

    @PostMapping("/api/game/users")
    public ResponseEntity<ApiResDto<GameUserCreateResDto>> createUser(
            @RequestBody GameUserCreateReqDto request) {
        GameUserCreateResDto response = gameUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResDto.success(response));
    }

    @PatchMapping("/api/game/users/{userId}")
    public ResponseEntity<ApiResDto<GameUserUpdateResDto>> updateUser(
            @PathVariable Long userId, @RequestBody GameUserUpdateReqDto request) {
        GameUserUpdateResDto response = gameUserService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResDto.success(response));
    }

    @GetMapping("/api/game/users/{userId}")
    public ResponseEntity<ApiResDto<GameUserResDto>> getUser(@PathVariable Long userId) {
        GameUserResDto response = gameUserService.getUser(userId);
        return ResponseEntity.ok(ApiResDto.success(response));
    }

    @GetMapping("/api/game/ranking")
    public ResponseEntity<ApiResDto<GameRankingListResDto>> getRanking(
            @RequestParam(required = false) Integer game) {
        GameRankingListResDto response = gameUserService.getRanking(game);
        return ResponseEntity.ok(ApiResDto.success(response));
    }
}