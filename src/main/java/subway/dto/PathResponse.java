package subway.dto;

import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public final class PathResponse {

    private final List<StationResponse> stations;
    private final long distance;

    private PathResponse(final List<StationResponse> stations, final long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(final List<Station> stations, final double distance) {
        throw new UnsupportedOperationException();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
