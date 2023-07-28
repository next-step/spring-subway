package subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {
    private final ErrorCode errorCode;

    public SubwayException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SubwayException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public SubwayException(ErrorCode errorCode, Object value) {
        super(errorCode.getMessage() + value);
        this.errorCode = errorCode;
    }

    public SubwayException(ErrorCode errorCode, Object value, Throwable cause) {
        super(errorCode.getMessage() + value, cause);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getCode();
    }
}
