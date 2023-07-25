package subway.ui.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

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

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(JsonProcessingException.class)
    public ErrorResponse jsonProcessingExceptionHandle() {
        return new ErrorResponse(BAD_REQUEST.value(), "입력 값이 잘못되었습니다.");
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ErrorResponse uniqueExceptionHandle() {
        return new ErrorResponse(BAD_REQUEST.value(), "중복된 이름을 입력했거나 값이 비어있습니다.");
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ErrorResponse handleSQLException() {
        return new ErrorResponse(INTERNAL_SERVER_ERROR.value(), "현재 서버에 문제가 있습니다.");
    }
}
