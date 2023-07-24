package subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import subway.dto.ErrorResponse;
import subway.exception.IncorrectRequestException;
import subway.exception.InternalStateException;

import java.sql.SQLException;

@RestControllerAdvice
public class SubwayAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException() {
        ErrorResponse response = new ErrorResponse("Invalid SQL Request.");
        return handleExceptionInternal(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IncorrectRequestException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectRequestException(IncorrectRequestException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return handleExceptionInternal(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalStateException.class)
    public ResponseEntity<ErrorResponse> handleInternalStateException(InternalStateException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return handleExceptionInternal(response, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorResponse response, HttpStatus status) {
        return ResponseEntity.status(status).body(response);
    }
}
