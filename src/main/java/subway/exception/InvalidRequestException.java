package subway.exception;

public class InvalidRequestException extends CustomException {

    public InvalidRequestException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
