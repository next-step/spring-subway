package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Void> handleSQLException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException() {
        return ResponseEntity.badRequest().build();
    }
}
