package subway.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.IllegalStationsException;

public class PathFinder {

    private final Set<Station> vertexes;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> weightedGraph;

    public PathFinder(List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> weightedGraph = createWeightedGraph(sections);
        this.vertexes = weightedGraph.vertexSet();
        this.weightedGraph = new DijkstraShortestPath<>(weightedGraph);
    }

    public double calculateShortestDistance(long sourceId, long targetId) {
        Station source = getVertex(sourceId)
            .orElseThrow(() -> new IllegalStationsException("존재하지 않는 역 정보입니다."));
        Station target = getVertex(targetId)
            .orElseThrow(() -> new IllegalStationsException("존재하지 않는 역 정보입니다."));

        validateSourceAndTarget(source, target);

        return weightedGraph.getPath(source, target).getWeight();
    }

    public List<Station> searchShortestPath(long sourceId, long targetId) {
        Station source = getVertex(sourceId)
            .orElseThrow(() -> new IllegalStationsException("존재하지 않는 역 정보입니다."));
        Station target = getVertex(targetId)
            .orElseThrow(() -> new IllegalStationsException("존재하지 않는 역 정보입니다."));

        validateSourceAndTarget(source, target);

        return weightedGraph.getPath(source, target).getVertexList();
    }

    private void validateSourceAndTarget(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalStationsException("출발역과 도착역은 달라야 합니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedGraph(
        List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
            DefaultWeightedEdge.class);

        // 역 추가
        graph.addVertex(sections.get(0).getUpStation());
        sections.forEach(section -> graph.addVertex(section.getDownStation()));

        // 양방향 경로 추가
        sections.forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance());
            graph.setEdgeWeight(graph.addEdge(section.getDownStation(), section.getUpStation()),
                section.getDistance());
        });

        return graph;
    }

    private Optional<Station> getVertex(final long stationId) {
        return vertexes.stream()
            .filter(station -> station.getId()==stationId)
            .findFirst();
    }
}
