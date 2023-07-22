package subway.exception.handler;

import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.SubwayException;
import subway.exception.dto.ExceptionResponse;

@RestControllerAdvice
public class CommonExceptionHandler {

    private static final String SQL_EXCEPTION_MESSAGE = "서버 내부에서 오류가 발생했습니다.";

    @ExceptionHandler(SubwayException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleSubwayException(final SubwayException exception) {
        return new ExceptionResponse(exception.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleSQLException() {
        return new ExceptionResponse(SQL_EXCEPTION_MESSAGE);
    }
}
