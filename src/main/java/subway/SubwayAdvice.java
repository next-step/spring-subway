package subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.ErrorResponse;
import subway.exception.IncorrectRequestException;
import subway.exception.InternalStateException;

import java.sql.SQLException;

@RestControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException() {
        ErrorResponse response = new ErrorResponse("Invalid SQL Error Caught.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IncorrectRequestException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectRequestException(IncorrectRequestException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InternalStateException.class)
    public ResponseEntity<ErrorResponse> handleInternalStateException(InternalStateException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException() {
        ErrorResponse response = new ErrorResponse("Unknown Error Caught.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
