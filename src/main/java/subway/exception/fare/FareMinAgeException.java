package subway.exception.fare;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class FareMinAgeException extends CommonException {

    private static final String MESSAGE = "나이는 최소 0살 이상어야 합니다.";

    public FareMinAgeException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
