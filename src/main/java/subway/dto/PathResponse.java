package subway.dto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
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
        return Objects.equals(stations, that.stations) && Objects.equals(distance,
                that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance);
    }

    @Override
    public String toString() {
        return "PathResponse{" +
                "stations=" + stations +
                ", distance=" + distance +
                '}';
    }
}
