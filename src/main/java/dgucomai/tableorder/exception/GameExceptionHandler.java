package dgucomai.tableorder.exception;

import dgucomai.tableorder.dto.res.ApiResDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GameExceptionHandler {

  @ExceptionHandler(GameApiException.class)
  public ResponseEntity<ApiResDto<Void>> handleGameApiException(GameApiException e) {
    return ResponseEntity.status(e.getStatus()).body(ApiResDto.error(e.getMessage()));
  }
}
