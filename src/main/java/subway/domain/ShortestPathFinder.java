package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class ShortestPathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final List<Station> stations;
    private final int distance;

    public ShortestPathFinder(final List<Section> sections, final Station source, final Station target) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        validateNotEqual(source, target);
        initializeVertex(sections, source, target);
        initializeEdge(sections);

        final GraphPath<Station, DefaultWeightedEdge> shortestPath = findShortestPath(source, target);
        this.stations = shortestPath.getVertexList();
        final double distance = shortestPath.getWeight();
        validateRange(distance);
        this.distance = (int) distance;
    }

    private void validateNotEqual(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }
    }

    private void initializeVertex(final List<Section> sections, final Station source, final Station target) {
        sections.stream()
            .flatMap(section -> section.getStations().stream())
            .distinct()
            .forEach(graph::addVertex);

        graph.addVertex(source);
        graph.addVertex(target);
    }

    private void initializeEdge(final List<Section> sections) {
        sections.forEach(
            section -> graph.setEdgeWeight(addEdge(section), section.getDistance()));
    }

    private DefaultWeightedEdge addEdge(final Section section) {
        return graph.addEdge(section.getUpStation(), section.getDownStation());
    }

    private GraphPath<Station, DefaultWeightedEdge> findShortestPath(final Station source, final Station target) {
        final DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(
            graph);
        final GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(source, target);

        validateConnection(path);
        return path;
    }

    private void validateConnection(final GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다");
        }
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
