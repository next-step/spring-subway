package subway.exception;

public class LineException extends RuntimeException {

    private final ErrorCode errorCode;

    public LineException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
