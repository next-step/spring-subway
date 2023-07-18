package subway.exception.dto;

public class ExceptionResponse {

    private final String message;

    public ExceptionResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
