package subway.domain;


import java.util.List;

public class Path {

    private final List<Station> stations;

    private final Long distance;

    private Path(List<Station> stations, Long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static Path of(
            final List<Section> allSections,
            final Station source,
            final Station target) {

        PathGraph pathGraph = new DijkstraPathGraph(allSections, source, target);
        return new Path(pathGraph.findRoute(), pathGraph.findDistance());
    }

    public List<Station> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
