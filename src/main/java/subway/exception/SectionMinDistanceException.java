package subway.exception;

import org.springframework.http.HttpStatus;

public class SectionMinDistanceException extends BadRequestException {

    private static final String MESSAGE = "최소 거리는 1이상 입니다.";

    public SectionMinDistanceException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
