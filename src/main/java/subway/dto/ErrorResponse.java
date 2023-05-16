package subway.dto;

public class ErrorResponse {

    private static final String BAD_REQUEST_CODE = "BAD_REQUEST";

    private String errorCode;
    private String errorMessage;

    private ErrorResponse() {
    }

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse ofBadRequest(String errorMessage) {
        return new ErrorResponse(BAD_REQUEST_CODE, errorMessage);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
