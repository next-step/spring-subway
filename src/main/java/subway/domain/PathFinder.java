package subway.domain;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.IllegalStationsException;
import subway.ui.dto.PathResponse;

public class PathFinder {

    private final List<Section> sections;

    public PathFinder(List<Section> sections) {
        this.sections = sections;
    }

    public PathResponse searchShortestPath(Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
            DefaultWeightedEdge.class);

        // 역 추가
        graph.addVertex(sections.get(0).getUpStation());
        sections.forEach(section -> graph.addVertex(section.getDownStation()));

        // 경로 추가
        sections.forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            graph.setEdgeWeight(graph.addEdge(section.getDownStation(), section.getUpStation()), section.getDistance());
        });

        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalStationsException("출발역 또는 도착역이 존재하지 않습니다.");
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        double distance = dijkstraShortestPath.getPathWeight(source, target);

        return new PathResponse(distance, shortestPath);
    }
}
