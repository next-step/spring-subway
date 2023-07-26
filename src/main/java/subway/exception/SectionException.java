package subway.exception;

public class SectionException extends RuntimeException {

    private final ErrorCode errorCode;

    public SectionException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
