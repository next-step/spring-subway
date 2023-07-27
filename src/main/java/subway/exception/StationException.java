package subway.exception;

public class StationException extends CustomException {

    public StationException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
