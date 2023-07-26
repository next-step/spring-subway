package subway.domain.exception;

public class StatusCodeException extends RuntimeException {

    private final String status;

    public StatusCodeException(String message, String status) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
