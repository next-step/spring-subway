package subway.exception;

import org.springframework.http.HttpStatus;

public class SectionNotConnectingStationException extends BadRequestException {

    private static final String MESSAGE = "상행역과 하행역이 같습니다.";

    public SectionNotConnectingStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }

}
