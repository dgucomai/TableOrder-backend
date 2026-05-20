package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.res.TokenResDto;
import dgucomai.tableorder.service.TokenService;
import java.util.LinkedHashMap;
import java.util.Map;
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
  public ResponseEntity<Map<String, Object>> getTokenCount(@PathVariable Long tableId) {
    TokenResDto response = tokenService.getTokenCount(tableId);

    Map<String, Object> customResponse = new LinkedHashMap<>();
    customResponse.put("success", true);
    customResponse.put("code", "OK");
    customResponse.put("message", "토큰 수 조회 성공");
    customResponse.put("data", response);

    return ResponseEntity.ok(customResponse);
  }
}
