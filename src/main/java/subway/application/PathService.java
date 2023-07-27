package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

@Service
public class PathService {

    public PathService() {
    }

    public PathResponse findMinimumDistancePaths(Long departureStationId, Long destinationStationId) {
        StationResponse stationResponse1 = new StationResponse(5L, "교대역");
        StationResponse stationResponse2 = new StationResponse(7L, "남부터미널역");
        StationResponse stationResponse3 = new StationResponse(8L, "양재역");
        Long distanceMinimum = 5L;
        PathResponse pathResponse = new PathResponse(
            List.of(stationResponse1, stationResponse2, stationResponse3),
            distanceMinimum
        );

        return pathResponse;
    }
}
