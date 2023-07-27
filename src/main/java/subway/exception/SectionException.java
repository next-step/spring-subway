package subway.exception;

public class SectionException extends CustomException {

    public SectionException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
