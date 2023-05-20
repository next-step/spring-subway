package subway.exception;

import org.springframework.http.HttpStatus;

public class SectionCrossException extends BadRequestException {

    private static final String MESSAGE = "갈림길을 생성할 수 없습니다.";

    public SectionCrossException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
