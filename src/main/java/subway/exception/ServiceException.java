package subway.exception;

public class ServiceException extends RuntimeException {

    private ErrorType errorType;

    public ServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

}
