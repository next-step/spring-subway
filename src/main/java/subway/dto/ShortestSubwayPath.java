package subway.dto;

import java.util.List;
import subway.domain.Station;

public class ShortestSubwayPath {

    private final List<Station> stations;
    private final double distance;

    public ShortestSubwayPath(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
