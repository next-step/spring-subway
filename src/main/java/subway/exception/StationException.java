package subway.exception;

public class StationException extends RuntimeException {

    private final ErrorCode errorCode;

    public StationException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
