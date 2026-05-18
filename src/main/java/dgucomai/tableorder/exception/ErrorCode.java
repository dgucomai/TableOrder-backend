package dgucomai.tableorder.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  MENU_NOT_FOUND("해당 메뉴를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MENU_SOLD_OUT("%s은(는) 현재 품절된 메뉴입니다.", HttpStatus.BAD_REQUEST),
  ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MENU_LIST_FETCH_FAILED("메뉴 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  TABLE_NOT_FOUND("해당 테이블을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  TABLE_SESSION_NOT_FOUND("현재 활성화된 테이블 세션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus status;

  ErrorCode(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
