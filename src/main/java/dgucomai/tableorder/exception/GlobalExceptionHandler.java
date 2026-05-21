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
public class GlobalExceptionHandler {

  private final LogService logService;

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResDto<Void>> handleCustomException(
      CustomException e, HttpServletRequest request) {
    logService.saveServiceLog(
        resolveActorType(request.getRequestURI()),
        request.getMethod() + " " + request.getRequestURI() + " 실패: " + e.getMessage());
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(ApiResDto.error(e.getMessage()));
  }

  private String resolveActorType(String uri) {
    if (uri.startsWith("/api/admin")) return "ADMIN";
    if (uri.startsWith("/api/staff") || uri.contains("/staff/")) return "STAFF";
    if (uri.startsWith("/api/game") || uri.startsWith("/api/tokens")) return "GAME";
    return "CLIENT";
  }
}
