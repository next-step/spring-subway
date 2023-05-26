package subway.exception.fare;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class FareMinDiscountException extends CommonException {

    private static final String MESSAGE = "거리는 최소 1 이상이어야야 합니다.";

    public FareMinDiscountException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }

}
