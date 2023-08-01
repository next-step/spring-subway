package subway.domain;


import java.util.List;

public class Path {

    private final List<Station> stations;
    private final long distance;


    public Path(final PathGraph pathGraph) {
        this.stations = pathGraph.getStations();
        this.distance = pathGraph.getDistance();
    }

    public List<Station> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
