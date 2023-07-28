package subway.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<ErrorResponse> handleSQLException(final SQLException e) {
        return ResponseEntity.internalServerError()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

    @ExceptionHandler(SubwayException.class)
    protected ResponseEntity<ErrorResponse> handleSubwayException(final SubwayException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(
            errorResponse,
            HttpStatus.valueOf(errorCode.getHttpStatus().value()
            )
        );
    }

}
