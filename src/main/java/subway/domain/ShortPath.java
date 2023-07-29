package subway.domain;

import java.util.List;

public class ShortPath {

    private List<Station> stations;
    private Distance distance;

    public ShortPath(final List<Station> stations, final Distance distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance.getValue();
    }
}
