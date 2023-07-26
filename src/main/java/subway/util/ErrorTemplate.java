package subway.util;

public class ErrorTemplate {

    private final String status;
    private final String message;

    private ErrorTemplate(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorTemplate from(String status, String message) {
        return new ErrorTemplate(status, message);
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
