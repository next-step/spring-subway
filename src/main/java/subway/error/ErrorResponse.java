package subway.error;

public class ErrorResponse {
    private int statusCode;
    private String message;

    public ErrorResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ErrorResponse() {
    }

    public static ErrorResponse of(final int statusCode, final String message) {
        return new ErrorResponse(statusCode, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
