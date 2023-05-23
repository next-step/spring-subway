package subway.exception;

import org.springframework.http.HttpStatus;

public class SectionSameStationException extends CommonException {

    private static final String MESSAGE = "상행역과 하행역이 같습니다.";

    public SectionSameStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
