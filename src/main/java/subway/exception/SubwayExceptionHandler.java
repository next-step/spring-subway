package subway.exception;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SQLException.class)
    public ErrorResponse handleSQLException() {
        return new ErrorResponse("예상하지 못한 예외가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException() {
        return new ErrorResponse("예상하지 못한 예외가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        String errorMessage = "입력값이 잘못되었습니다.\n";
        return errorMessage + String.join(", ", getFieldErrorMessages(ex));
    }

    private List<String> getFieldErrorMessages(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream().map(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            return fieldName + ": " + message;
        }).collect(Collectors.toUnmodifiableList());
    }
}
