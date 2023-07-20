package subway.ui.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalArgumentExceptionHandle(IllegalArgumentException exception) {
        return new ErrorResponse(BAD_REQUEST.value(), exception.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse illegalStateExceptionHandle(IllegalStateException exception) {
        return new ErrorResponse(INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }
}
