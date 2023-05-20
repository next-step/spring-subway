package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleSQLException(SQLException e) {
        return ResponseEntity.badRequest().body("SQL 오류");
    }

    @ExceptionHandler({NoSuchElementException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleNoSuchElementException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
