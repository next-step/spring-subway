package subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class Path {

    private final List<Station> path;
    private final Distance distance;

    public Path(List<Sections> allSections, Station source, Station target) {

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Sections sections : allSections) {
            for (Station station : sections.toStations()) {
                graph.addVertex(station);
            }
            for (Section section : sections.getSections()) {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance().getValue());
            }
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        this.path = dijkstraShortestPath.getPath(source, target).getVertexList();
        this.distance = new Distance((int) dijkstraShortestPath.getPathWeight(source, target));
    }

    public List<Station> getPath() {
        return path;
    }

    public Distance getDistance() {
        return distance;
    }
}
