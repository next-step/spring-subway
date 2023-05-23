package subway.exception.section;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class SectionCrossException extends CommonException {

    private static final String MESSAGE = "갈림길을 생성할 수 없습니다.";

    public SectionCrossException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
