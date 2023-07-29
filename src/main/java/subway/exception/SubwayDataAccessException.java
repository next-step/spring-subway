package subway.exception;

public class SubwayDataAccessException extends SubwayException {

    public SubwayDataAccessException(final String message) {
        super(message);
    }

    public SubwayDataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
