package subway.dto;

import java.util.List;
import subway.domain.Station;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Long distance;

    public PathResponse(final List<StationResponse> stations, final Long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(final List<Station> stations, final Long distance) {
        return new PathResponse(StationResponse.of(stations), distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
