package subway.exception;

public class IncorrectRequestException extends CustomException {

    public IncorrectRequestException(ErrorCode errorCode, String request) {
        super(errorCode, errorCode.getDescription() + " " + request);
    }
}
