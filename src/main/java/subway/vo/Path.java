package subway.vo;

import subway.domain.Station;

import java.util.List;

public class Path {
    private final int distance;
    private final List<Station> stations;

    public Path(int distance, List<Station> stations) {
        this.distance = distance;
        this.stations = List.copyOf(stations);
    }

    public int getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }
}
