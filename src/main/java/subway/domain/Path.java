package subway.domain;


import java.util.List;

public class Path {

    private final List<Station> stations;

    private final Long distance;

    public Path(final List<Section> allSections, final Station source, final Station target) {
        PathGraph pathGraph = new DijkstraPathGraph(allSections, source, target);
        this.stations = pathGraph.findRoute();
        this.distance = pathGraph.findDistance();
    }

    public List<Station> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
