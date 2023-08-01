package subway.dto;

import subway.domain.Path;
import subway.domain.Station;

import java.util.List;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Long distance;

    private PathResponse(final List<Station> stations, final Long distance) {
        this.stations = StationResponse.of(stations);
        this.distance = distance;
    }

    public static PathResponse from(final Path path) {
        return new PathResponse(path.getStations(), path.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
