package subway.exception.section;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class SectionNotConnectingStationException extends CommonException {

    private static final String MESSAGE = "추가될 상행역은 노선에 등록 되어있는 하행종점역이 아닙니다.";

    public SectionNotConnectingStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
