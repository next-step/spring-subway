package subway.config.exceptionHandle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "알 수 없는 에러가 발생했습니다.";

    private UUID generateLogId(Exception ex) {
        UUID uuid = UUID.randomUUID();
        log.error("## error : {}, {}", uuid, ex.getClass().getSimpleName(), ex);
        return uuid;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return ErrorResponse.of(generateLogId(ex), ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        return ErrorResponse.of(generateLogId(ex), ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        return ErrorResponse.of(generateLogId(ex), new Exception(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
