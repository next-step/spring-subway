package subway.domain;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.domain.exception.StatusCodeException;
import subway.domain.response.PathResponse;
import subway.domain.status.PathExceptionStatus;

public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public Path(List<Section> sections) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        initVertex(sections);
        connectVertex(sections);
    }

    private void initVertex(List<Section> sections) {
        sections.forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
        });
    }

    private void connectVertex(List<Section> sections) {
        for (Section currentSection : sections) {
            graph.setEdgeWeight(graph.addEdge(currentSection.getUpStation(), currentSection.getDownStation()),
                    currentSection.getDistance());
        }
    }

    public PathResponse minimumPath(Station sourceStation, Station targetStation) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        GraphPath<Station, DefaultWeightedEdge> dijkstraResult = dijkstra(sourceStation, targetStation,
                dijkstraShortestPath);

        List<Station> vertexes = dijkstraResult.getVertexList();
        int weight = (int) dijkstraResult.getWeight();

        return new PathResponse(vertexes.stream()
                .map(vertex -> new PathResponse.StationResponse(vertex.getId(), vertex.getName()))
                .collect(Collectors.toList()),
                weight);
    }

    private GraphPath<Station, DefaultWeightedEdge> dijkstra(Station sourceStation, Station targetStation,
            DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath) {
        try {
            return dijkstraShortestPath.getPath(sourceStation, targetStation);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new StatusCodeException(
                    MessageFormat.format("sourceStation \"{0}\" 에서 targetStation \"{1}\" 으로 가는 경로를 찾을 수 없습니다.",
                            sourceStation, targetStation), PathExceptionStatus.CANNOT_FIND_PATH.getStatus());
        }
    }

}
