package subway.ui.exception;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    public GlobalControllerAdvice(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({SQLException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleSQLException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(joinFieldErrorMessages(e)));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationException(Exception e) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage()));
    }

    private String joinFieldErrorMessages(final MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
            .map(this::resolveFieldErrorMessage)
            .collect(Collectors.joining(" "));
    }

    private String resolveFieldErrorMessage(FieldError error) {
        Object[] arguments = error.getArguments();
        Locale locale = LocaleContextHolder.getLocale();

        return Arrays.stream(error.getCodes())
            .map(c -> {
                try {
                    return messageSource.getMessage(c, arguments, locale);
                } catch (NoSuchMessageException e) {
                    return null;
                }
            }).filter(Objects::nonNull)
            .findFirst()
            .orElse(error.getDefaultMessage());
    }
}
