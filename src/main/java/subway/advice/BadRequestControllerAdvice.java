package subway.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.util.ErrorTemplate;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class BadRequestControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorTemplate> catchIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(ErrorTemplate.of(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
