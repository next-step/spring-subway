package subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> handleSQLException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(HttpStatus.BAD_REQUEST, e));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleApiException(BadRequestException e) {
        return ResponseEntity.status(e.getStatus())
            .body(new ErrorResponse(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage());
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "예상치 못한 에러가 발생했습니다. 잠시후 요청 바랍니다."));
    }
}
