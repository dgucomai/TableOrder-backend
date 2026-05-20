package dgucomai.tableorder.exception;

import dgucomai.tableorder.dto.res.ApiResDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResDto<Void>> handleCustomException(CustomException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(ApiResDto.error(e.getMessage()));
  }
}
