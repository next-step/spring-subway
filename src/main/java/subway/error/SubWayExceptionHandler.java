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
    public ResponseEntity<ErrorData> handleSQLException() {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorData.of(BAD_REQUEST.value(), "처리할 수 없는 입력이 주어졌습니다."));
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorData> handleArgumentExceptionException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorData.of(BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler({
            IllegalStateException.class
    })
    public ResponseEntity<ErrorData> handleIllegalStateExceptionException() {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorData.of(INTERNAL_SERVER_ERROR.value(), "서버가 잘못 설계되었습니다. 문의 주십시오."));
    }

}
