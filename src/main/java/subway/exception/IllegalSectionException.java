package subway.exception;

public class IllegalSectionException extends SubwayBadRequestException {

    public IllegalSectionException(final String message) {
        super(message);
    }
}
