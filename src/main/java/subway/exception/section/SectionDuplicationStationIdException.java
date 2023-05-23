package subway.exception.section;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class SectionDuplicationStationIdException extends CommonException {

    private static final String MESSAGE = "중복된 역이 있습니다.";

    public SectionDuplicationStationIdException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
