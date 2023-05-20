package subway.exception;

import org.springframework.http.HttpStatus;

public class SectionDuplicationStationIdException extends BadRequestException {

    private static final String MESSAGE = "중복된 역이 있습니다.";

    public SectionDuplicationStationIdException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
