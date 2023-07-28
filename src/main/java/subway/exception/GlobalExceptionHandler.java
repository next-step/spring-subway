package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.ExceptionResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ExceptionResponse> handleSubwayException(SubwayException e) {
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .get()
                .getDefaultMessage();
        return new ResponseEntity<>(new ExceptionResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }
}
