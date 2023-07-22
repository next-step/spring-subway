package subway.error;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class SubWayExceptionHandler {

    @ExceptionHandler({
            SQLException.class,
    })
    public ResponseEntity<Void> handleSQLException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
    })
    public ResponseEntity<String> handleArgumentExceptionException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body(exception.getMessage());
    }
}
