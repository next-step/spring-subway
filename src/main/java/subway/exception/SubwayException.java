package subway.exception;

public abstract class SubwayException extends RuntimeException {

    protected SubwayException(final String message) {
        super(message);
    }

    protected SubwayException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
