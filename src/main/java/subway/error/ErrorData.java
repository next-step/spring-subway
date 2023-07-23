package subway.error;

public class ErrorData {
    private int statusCode;
    private String message;

    public ErrorData(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ErrorData() {
    }

    public static ErrorData of(final int statusCode, final String message) {
        return new ErrorData(statusCode, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
