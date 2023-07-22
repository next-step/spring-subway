package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> sqlExceptionHandler(SQLException exception) {
        return ResponseEntity.internalServerError().body(exception.getLocalizedMessage());
    }

    @ExceptionHandler(SubwayBadRequestException.class)
    public ResponseEntity<String> illegalSectionExceptionHandler(SubwayBadRequestException exception) {
        return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
    }
}
