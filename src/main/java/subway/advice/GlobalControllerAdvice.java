package subway.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> catchIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
