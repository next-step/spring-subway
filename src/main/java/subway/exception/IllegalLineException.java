package subway.exception;

public final class IllegalLineException extends RuntimeException {

    public IllegalLineException(final String message) {
        super(message);
    }
}
