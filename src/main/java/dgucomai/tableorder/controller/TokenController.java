package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.TokenResDto;
import dgucomai.tableorder.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;

  @GetMapping("/{tableId}")
  public ResponseEntity<ApiResDto<TokenResDto>> getTokenCount(@PathVariable Long tableId) {
    TokenResDto response = tokenService.getTokenCount(tableId);
    return ResponseEntity.ok(ApiResDto.success(response));
  }
}
