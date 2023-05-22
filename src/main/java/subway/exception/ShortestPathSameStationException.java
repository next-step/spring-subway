package subway.exception;

import org.springframework.http.HttpStatus;

public class ShortestPathSameStationException extends BadRequestException {

    private static final String MESSAGE = "출발역과 도착역이 같습니다.";

    public ShortestPathSameStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
