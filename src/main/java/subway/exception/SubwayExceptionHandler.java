package subway.exception;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class SubwayExceptionHandler {

    private Logger logger;


    public SubwayExceptionHandler() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

    @ExceptionHandler(SubwayException.class)
    protected ResponseEntity<ErrorResponse> handleSubwayException(SubwayException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        e.printStackTrace();
        return new ResponseEntity<>(errorResponse,
            HttpStatus.valueOf(errorCode.getHttpStatus().value()));
    }
}
