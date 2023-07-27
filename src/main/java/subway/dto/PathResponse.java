package subway.dto;

import subway.domain.Distance;
import subway.domain.Station;

import java.util.List;

public class PathResponse {

    private List<Station> stations;
    private int distance;

    public PathResponse() {
    }

    public PathResponse(List<Station> stations, Distance distance) {
        this(stations, distance.getValue());
    }

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
