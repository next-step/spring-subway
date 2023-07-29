package subway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.ErrorResponse;
import subway.exception.CustomException;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.sql.SQLException;

@RestControllerAdvice
public class SubwayAdvice {

    private static final String EXCEPTION_INFO = "예외 발생: 메시지: {}, Stack Trace: {}";
    private final Logger logger = LoggerFactory.getLogger(SubwayAdvice.class);

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_DB_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getDescription());
        logger.error(EXCEPTION_INFO, e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(IncorrectRequestException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), e.getMessage());
        logger.error(EXCEPTION_INFO, e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus())).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getDescription());
        logger.error(EXCEPTION_INFO, e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
