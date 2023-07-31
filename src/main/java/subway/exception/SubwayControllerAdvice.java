package subway.exception;

import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> sqlExceptionHandler(SQLException exception) {
        return ResponseEntity.internalServerError().body(exception.getLocalizedMessage());
    }

    @ExceptionHandler(SubwayBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> illegalSectionExceptionHandler(SubwayBadRequestException exception) {
        return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
    }
}
