package subway.exception;

import org.springframework.http.HttpStatus;

public class StationNotFoundException extends CommonException {

    private static final String MESSAGE = "해당 지하철역이 없습니다.";

    public StationNotFoundException() {
        super(HttpStatus.NOT_FOUND, MESSAGE);
    }
}
