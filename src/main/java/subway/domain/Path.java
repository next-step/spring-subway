package subway.domain;


import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class Path {

    private final PathGraph pathGraph;
    private final GraphPath<Station, DefaultWeightedEdge> path;

    public Path(final PathGraph pathGraph, final Station start, final Station end) {
        this.pathGraph = pathGraph;
        this.path = pathGraph.createPath(start, end);
    }

    public List<Station> getStations() {
        return path.getVertexList();
    }

    public long getDistance() {
        return (long) path.getWeight();
    }
}
