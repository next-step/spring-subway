package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathGraph {
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathGraph(WholeSection wholeSection) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Station station : wholeSection.getAllStations()) {
            graph.addVertex(station);
        }
        for (Section section : wholeSection.getAllSections()) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public ShortPath getShortPath(Station source, Station target) {
        validateSourceAndTarget(source, target);
        final GraphPath<Station, DefaultWeightedEdge> sourceToTargetGraphPath = getSourceToTargetGraphPath(source, target);
        final List<Station> stations = sourceToTargetGraphPath.getVertexList();
        final Distance distance = new Distance((long) sourceToTargetGraphPath.getWeight());
        return new ShortPath(stations, distance);
    }

    private void validateSourceAndTarget(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같은 경우 최단 거리를 구할 수 없습니다.");
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> getSourceToTargetGraphPath(final Station source, final Station target) {
        try {
            final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
            throwIfNotFoundPath(path);
            return path;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다.", e);
        }
    }

    private void throwIfNotFoundPath(final GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new IllegalArgumentException("최단 거리를 구할 수 없습니다.");
        }
    }

}
