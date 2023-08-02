package subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class SubwayControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(SubwayException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalSubwayExceptionHandler(SubwayException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalRequestExceptionHandler(HttpMessageNotReadableException exception) {
        String message = extractMessage(exception);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse runtimeExceptionHandler(Exception exception) {
        log.error(exception.getMessage());
        return new ErrorResponse("서버 오류가 발생했습니다.");
    }

    private static String extractMessage(final HttpMessageNotReadableException exception) {
        if (exception.getRootCause() != null) {
            return exception.getRootCause().getMessage();
        }
        return "";
    }
}
