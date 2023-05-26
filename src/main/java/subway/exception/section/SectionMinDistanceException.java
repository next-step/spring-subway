package subway.exception.section;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class SectionMinDistanceException extends CommonException {

    private static final String MESSAGE = "최소 거리는 1이상 입니다.";

    public SectionMinDistanceException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
