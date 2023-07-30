package subway.dto;

import subway.exception.ErrorCode;

public class ErrorResponse {

    private final String code;
    private final String message;

    public ErrorResponse(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorResponse from(final String code, final String message) {
        return new ErrorResponse(code, message);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
