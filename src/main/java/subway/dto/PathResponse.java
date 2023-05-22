package subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import subway.domain.Station;

public class PathResponse {

    private final Integer distance;
    private final List<StationResponse> stations;
    public PathResponse(Integer distance, List<StationResponse> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public static PathResponse of(List<Station> stations, int distance) {
        return new PathResponse(distance, stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toUnmodifiableList()));
    }

    public Integer getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
