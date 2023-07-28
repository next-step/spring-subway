package subway.exception;

import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String sqlExceptionHandler(SQLException exception) {
        return exception.getLocalizedMessage();
    }

    @ExceptionHandler(SubwayBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String illegalSectionExceptionHandler(SubwayBadRequestException exception) {
        return exception.getLocalizedMessage();
    }
}
