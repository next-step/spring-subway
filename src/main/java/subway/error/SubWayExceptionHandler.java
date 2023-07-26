package subway.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@RestControllerAdvice
public class SubWayExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SubWayExceptionHandler.class);

    @ExceptionHandler({
            SQLException.class,
    })
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException exception) {
        printLog(exception);
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(BAD_REQUEST.value(), "처리할 수 없는 입력이 주어졌습니다."));
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleArgumentExceptionException(IllegalArgumentException exception) {
        printLog(exception);
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler({
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalStateExceptionException(IllegalStateException exception) {
        printLog(exception);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), "접근해서는 안되는 영역에 접근했습니다."));
    }

    private void printLog(final Exception exception) {
        logger.info(exception.getMessage());
    }

}
