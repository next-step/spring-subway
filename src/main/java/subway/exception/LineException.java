package subway.exception;

public class LineException extends CustomException {

    public LineException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
