package subway.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.ErrorResponse;
import subway.exception.CustomException;
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
    protected ResponseEntity<ErrorResponse> handleSectionException(final CustomException e) {
        log.error(e.getMessage());

        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ErrorResponse handleDataIntegrityViolationException(
            final DataIntegrityViolationException e) {
        log.error(e.getMessage());

        return ErrorResponse.of(ErrorCode.DATABASE_EXISTS);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    protected ErrorResponse handleDataAccessException(final DataAccessException e) {
        log.error(e.getMessage());

        return ErrorResponse.of(ErrorCode.DATABASE_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    protected ErrorResponse handleRuntimeException(final RuntimeException e) {
        log.error(e.getMessage());

        return ErrorResponse.of(ErrorCode.UNKNOWN_ERROR);
    }
}
