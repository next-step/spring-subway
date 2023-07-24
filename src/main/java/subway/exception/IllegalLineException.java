package subway.exception;

public class IllegalLineException extends SubwayBadRequestException {

    public IllegalLineException(final String message) {
        super(message);
    }
}
