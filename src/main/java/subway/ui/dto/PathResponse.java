package subway.ui.dto;

import java.util.List;
import java.util.stream.Collectors;
import subway.domain.Station;

public class PathResponse {

    private final double distance;
    private final List<StationResponse> stations;

    public PathResponse(double distance, List<Station> stations) {
        this.distance = distance;
        this.stations = stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public double getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
