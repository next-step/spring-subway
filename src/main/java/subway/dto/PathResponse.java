package subway.dto;

import java.util.List;
import subway.domain.ShortestPath;
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

    public static PathResponse of(final ShortestPath shortestPath) {
        return new PathResponse(shortestPath.getStations(), shortestPath.getDistance());
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
