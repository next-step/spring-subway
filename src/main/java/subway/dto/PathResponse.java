package subway.dto;

import java.util.List;
import subway.domain.ShortestPathFinder;
import subway.domain.Station;

public class PathResponse {

    private List<Station> stations;
    private int distance;

    private PathResponse() {
    }

    public PathResponse(final List<Station> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(final ShortestPathFinder pathFinder) {
        return new PathResponse(pathFinder.getStations(), pathFinder.getDistance());
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
