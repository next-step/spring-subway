package subway.exception;

public class IllegalLineException extends RuntimeException {

    public IllegalLineException(final String message) {
        super(message);
    }
}
