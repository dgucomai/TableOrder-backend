package dgucomai.tableorder.exception;

import dgucomai.tableorder.dto.res.ApiResDto;
import dgucomai.tableorder.logs.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GameExceptionHandler {

  private final LogService logService;

  @ExceptionHandler(GameApiException.class)
  public ResponseEntity<ApiResDto<Void>> handleGameApiException(
      GameApiException e, HttpServletRequest request) {
    logService.saveServiceLog(
        "GAME", request.getMethod() + " " + request.getRequestURI() + " 실패: " + e.getMessage());
    return ResponseEntity.status(e.getStatus()).body(ApiResDto.error(e.getMessage()));
  }
}
