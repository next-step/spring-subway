package subway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.ErrorResponse;
import subway.exception.ErrorCode;
import subway.exception.InvalidRequestException;
import subway.exception.LineException;
import subway.exception.SectionException;
import subway.exception.StationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({LineException.class, SectionException.class, StationException.class,
            InvalidRequestException.class})
    protected ResponseEntity<ErrorResponse> handleSectionException(final RuntimeException e) {
        log.error(e.getMessage());

        ErrorCode errorCode;

        if (e instanceof LineException) {
            errorCode = ((LineException) e).getErrorCode();
        } else if (e instanceof SectionException) {
            errorCode = ((SectionException) e).getErrorCode();
        } else if (e instanceof StationException) {
            errorCode = ((StationException) e).getErrorCode();
        } else {
            errorCode = ((InvalidRequestException) e).getErrorCode();
        }

        final ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            final DataIntegrityViolationException e) {
        log.error(e.getMessage());

        return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.EXISTS_DATA));
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<ErrorResponse> handleDataAccessException(final DataAccessException e) {
        log.error(e.getMessage());

        return ResponseEntity.internalServerError().body(ErrorResponse.of(ErrorCode.DATABASE_ERROR));
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException e) {
        log.error(e.getMessage());

        return ResponseEntity.internalServerError().body(ErrorResponse.of(ErrorCode.UNKNOWN_ERROR));
    }
}
