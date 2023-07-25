package subway.advice;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.util.ErrorTemplate;

@Order
@RestControllerAdvice
class InternalErrorControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(SQLException.class)
    ResponseEntity<ErrorTemplate> handleSQLException(SQLException sqlException) {
        logger.error(sqlException.getMessage());
        return new ResponseEntity<>(ErrorTemplate.of("SQL fail"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ErrorTemplate> catchIllegalStateException(IllegalStateException illegalStateException) {
        logger.error(illegalStateException.getMessage());
        return new ResponseEntity<>(ErrorTemplate.of("Illegal State Fail"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorTemplate> catchUnknownException(Exception exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(ErrorTemplate.of("Unhandled exception"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
