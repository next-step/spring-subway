package subway.exception;

import org.springframework.http.HttpStatus;

public class CommonException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public CommonException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
