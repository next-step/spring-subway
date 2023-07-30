package subway.exception;

public class FindPathException extends CustomException {

    public FindPathException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
