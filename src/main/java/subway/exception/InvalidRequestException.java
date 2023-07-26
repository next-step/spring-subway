package subway.exception;

public class InvalidRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidRequestException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
