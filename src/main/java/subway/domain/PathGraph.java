package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathGraph {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;


    public PathGraph(final List<Section> sections) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sections.forEach(section -> graph.addVertex(section.getDownStation()));
        sections.forEach(section -> graph.addVertex(section.getUpStation()));
        sections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation()),
                section.getDistance()
            )
        );
    }

    public PathGraph(Sections sections) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sections.getStationsCache().forEach(station -> graph.addVertex(station));
        sections.getSections().forEach(section -> graph.setEdgeWeight(
                graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation()),
                section.getDistance()
            )
        );
    }

    public GraphPath<Station, DefaultWeightedEdge> createRoute(final Station start,
        final Station end) {
        validatePathConnect(start, end);

        GraphPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph).getPath(
            start, end);
        if (path == null) {
            throw new IllegalArgumentException("출발점과 도착역이 연결되어 있지 않습니다.");
        }
        return path;
    }

    public void validatePathConnect(final Station start, final Station end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("출발점과 도착역이 같다면, 길을 생성할 수 없습니다.");
        }
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("출발역이 존재하지 않습니다.");
        }
        if (!graph.containsVertex(end)) {
            throw new IllegalArgumentException("도착역이 존재하지 않습니다.");
        }
    }

}