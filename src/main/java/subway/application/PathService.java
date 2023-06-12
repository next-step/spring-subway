package subway.application;

import org.springframework.stereotype.Service;
import subway.dto.PathResponse;

import java.util.List;

@Service
public class PathService {

    public PathResponse findShortestPath(Long departureStationId, Long arrivalStationId) {
        // TODO PathService 구현
        return new PathResponse(List.of(), 0, 0);
    }

}
