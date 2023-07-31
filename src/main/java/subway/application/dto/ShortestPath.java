package subway.application.dto;

import java.util.List;
import subway.domain.Station;

public class ShortestPath {

    private final double distance;
    private final List<Station> stations;

    public ShortestPath(double distance, List<Station> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public double getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }
}
