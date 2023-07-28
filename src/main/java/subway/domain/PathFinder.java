package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

    private final List<Section> sections;

    public PathFinder(final List<Section> sections) {
        this.sections = sections;
    }

    public ShortestPath findShortestPath(final Station source, final Station target) {
        validateNotEqual(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph
            = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        initializeVertex(source, target, graph);
        initializeEdge(graph);

        final DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(
            graph);
        final GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(source, target);

        validateConnection(path);

        return new ShortestPath(path.getVertexList(), path.getWeight());
    }

    private void initializeVertex(final Station source, final Station target,
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
            .flatMap(section -> section.getStations().stream())
            .distinct()
            .forEach(graph::addVertex);

        graph.addVertex(source);
        graph.addVertex(target);
    }

    private void initializeEdge(final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.forEach(
            section -> graph.setEdgeWeight(addEdge(graph, section), section.getDistance()));
    }

    private DefaultWeightedEdge addEdge(
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final Section section) {
        return graph.addEdge(section.getUpStation(), section.getDownStation());
    }

    private void validateNotEqual(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }
    }

    private void validateConnection(final GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다");
        }
    }
}
