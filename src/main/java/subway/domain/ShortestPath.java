package subway.domain;

import java.util.List;

public class ShortestPath {

    private final List<Station> stations;
    private final int distance;

    public ShortestPath(final List<Station> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public ShortestPath(final List<Station> stations, final double distance) {
        this.stations = stations;
        validateRange(distance);
        this.distance = (int) distance;
    }

    private void validateRange(final double distance) {
        if (distance > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("경로 길이가 허용 범위를 초과합니다.");
        }
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
