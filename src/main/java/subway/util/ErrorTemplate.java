package subway.util;

public class ErrorTemplate {

    private final String message;

    private ErrorTemplate(String message) {
        this.message = message;
    }

    public static ErrorTemplate of(String message) {
        return new ErrorTemplate(message);
    }

    public String getMessage() {
        return message;
    }
}
