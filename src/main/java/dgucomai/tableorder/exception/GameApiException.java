package dgucomai.tableorder.exception;

import org.springframework.http.HttpStatus;

public class GameApiException extends RuntimeException {

  private final HttpStatus status;
  private final String code;

  public GameApiException(HttpStatus status, String message) {
    this(status, "GAME_API_ERROR", message);
  }

  public GameApiException(HttpStatus status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }
}
