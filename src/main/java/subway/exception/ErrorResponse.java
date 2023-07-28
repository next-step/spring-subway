package subway.exception;

public class ErrorResponse {

    private final int statusCode;
    private final String message;

    public ErrorResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static ErrorResponse of(final int statusCode, final String message) {
        return new ErrorResponse(statusCode, message);
    }

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.getMessage());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
