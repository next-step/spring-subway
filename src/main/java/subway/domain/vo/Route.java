package subway.domain.vo;

import subway.domain.entity.Station;

import java.util.List;

public class Route {
    private final List<Station> stations;
    private final int distance;

    public Route(List<Station> stations, int distance) {
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
