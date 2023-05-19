package subway.exception;

public class ServiceException extends RuntimeException {

    private final ErrorType errorType;

    public ServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

}
