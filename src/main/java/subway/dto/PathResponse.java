package subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import subway.domain.Fare;
import subway.domain.Station;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Integer distance;
    private final Fare fare;

    public PathResponse(List<StationResponse> stations, Integer distance, Fare fare) {
        this.stations = stations;
        this.distance = distance;
        this.fare = fare;
    }

    public static PathResponse of(List<Station> stations, int distance, Fare fare) {
        return new PathResponse(
            stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList()),
            distance,
            fare);
    }

    public Integer getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getFare() {
        return fare.getFare();
    }
}
