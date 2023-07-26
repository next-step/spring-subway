package subway.exception;

public class InternalStateException extends CustomException {

    public InternalStateException(ErrorCode errorCode, String request) {
        super(errorCode, errorCode.getDescription() + " " + request);
    }
}
