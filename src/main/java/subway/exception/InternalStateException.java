package subway.exception;

public class InternalStateException extends IllegalStateException{
    public InternalStateException(String errorMessage) {
        super(errorMessage);
    }
}
