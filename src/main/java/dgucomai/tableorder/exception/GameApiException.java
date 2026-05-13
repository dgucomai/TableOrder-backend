package dgucomai.tableorder.exception;

import org.springframework.http.HttpStatus;

public class GameApiException extends RuntimeException {

    private final HttpStatus status;

    public GameApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
