package subway.exception;

public final class IllegalRequestException extends RuntimeException {

    public IllegalRequestException(final String message) {
        super(message);
    }
}
