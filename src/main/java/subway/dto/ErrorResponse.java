package subway.dto;

import subway.exception.SubwayBaseException;

public class ErrorResponse {

    private Integer code;

    private String message;

    public ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(SubwayBaseException exception) {
        this.code = exception.getCode();
        this.message = exception.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
