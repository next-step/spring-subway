package subway.exception;

import org.springframework.http.HttpStatus;

public class FareMinDiscountException extends CommonException {

    private static final String MESSAGE = "거리는 최소 1 이상이어야야 합니다.";

    public FareMinDiscountException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }

}
