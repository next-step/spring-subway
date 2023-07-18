package subway.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.SubwayException;
import subway.exception.dto.ExceptionResponse;

@RestControllerAdvice
public class RuntimeExceptionHandler {

    @ExceptionHandler(SubwayException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleSubwayException(final SubwayException exception) {
        return new ExceptionResponse(exception.getMessage());
    }
}
