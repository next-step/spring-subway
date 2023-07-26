package subway.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.SubwayException;
import subway.exception.dto.ExceptionResponse;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubwayException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleSubwayException(final SubwayException exception) {
        return new ExceptionResponse(exception.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleSQLException(final SQLException exception) {
        exception.printStackTrace();
        return new ExceptionResponse("올바르지 않은 데이터 접근입니다.");
    }
}
