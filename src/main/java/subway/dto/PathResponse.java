package subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import subway.domain.Path;
import subway.domain.Station;

public class PathResponse {

    private final List<StationResponse> stations;

    private final Long distance;

    private PathResponse(List<StationResponse> stations, Long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<Station> stations, Long distance) {
        return new PathResponse(
                stations.stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toUnmodifiableList()),
                distance);
    }

    public static PathResponse of(Path path) {
        return new PathResponse(
                path.getStations().stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toUnmodifiableList()),
                path.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
