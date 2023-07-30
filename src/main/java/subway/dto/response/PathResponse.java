package subway.dto.response;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import subway.domain.Station;

public class PathResponse {

    List<StationResponse> stations;

    private int distance;

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PathResponse that = (PathResponse) o;
        return distance == that.distance && Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance);
    }
}
