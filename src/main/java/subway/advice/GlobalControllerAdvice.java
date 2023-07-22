package subway.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.util.ErrorTemplate;

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorTemplate> catchIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(ErrorTemplate.of(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ErrorTemplate> catchIllegalStateException(IllegalStateException illegalStateException) {
        return new ResponseEntity<>(ErrorTemplate.of(illegalStateException.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
