package subway.error;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class SubWayExceptionHandler {

    @ExceptionHandler({
            SQLException.class,
    })
    public ResponseEntity<ErrorResponse> handleSQLException() {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(BAD_REQUEST.value(), "처리할 수 없는 입력이 주어졌습니다."));
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleArgumentExceptionException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler({
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalStateExceptionException(IllegalStateException exception) {
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
    }

}
