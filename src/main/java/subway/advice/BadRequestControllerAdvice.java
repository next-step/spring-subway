package subway.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.domain.exception.StatusCodeException;
import subway.util.ErrorTemplate;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class BadRequestControllerAdvice {

    @ExceptionHandler(StatusCodeException.class)
    ResponseEntity<ErrorTemplate> catchIllegalArgumentException(StatusCodeException statusCodeException) {
        return new ResponseEntity<>(
                ErrorTemplate.from(statusCodeException.getStatus(), statusCodeException.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
