package subway.exception.shortestpath;

import org.springframework.http.HttpStatus;
import subway.exception.CommonException;

public class ShortestPathSameStationException extends CommonException {

    private static final String MESSAGE = "출발역과 도착역이 같습니다.";

    public ShortestPathSameStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
