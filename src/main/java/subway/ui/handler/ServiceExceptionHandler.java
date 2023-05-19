package subway.ui.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.ErrorResponse;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ServiceExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "올바르지 않은 요청입니다.";

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        ErrorType errorType = e.getErrorType();
        ErrorResponse response = new ErrorResponse(errorType.name(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        ErrorResponse response = ErrorResponse.ofBadRequest(getErrorMessage(bindingResult));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElseGet(() -> DEFAULT_ERROR_MESSAGE);
    }
}
