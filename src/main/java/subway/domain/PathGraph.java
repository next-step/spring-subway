package subway.domain;

import static subway.exception.ErrorCode.NOT_CONNECTED_BETWEEN_START_AND_END_PATH;
import static subway.exception.ErrorCode.NOT_FOUND_START_PATH_POINT;
import static subway.exception.ErrorCode.SAME_START_END_PATH_POINT;

import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.dto.PathResponse;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

public class PathGraph {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathGraph(final Set<Station> vertex, List<Section> edges) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initializeVertex(vertex);
        initializeEdges(edges);
    }

    private void initializeEdges(List<Section> edges) {
        edges.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
            )
        );
    }

    private void initializeVertex(Set<Station> vertex) {
        vertex.forEach(graph::addVertex);
    }

    public PathGraph(final Sections sections) {
        this(sections.getStationsCache(), sections.getSections());
    }

    public PathGraph(final List<Section> sections) {
        this(new Sections(sections));
    }

    public PathResponse createPath(final Station start, final Station end) {
        validatePathConnect(start, end);

        GraphPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph)
            .getPath(start, end);
        if (path == null) {
            throw new SubwayException(NOT_CONNECTED_BETWEEN_START_AND_END_PATH);
        }
        return PathResponse.of(path);
    }

    private void validatePathConnect(final Station start, final Station end) {
        if (start.equals(end)) {
            throw new SubwayException(SAME_START_END_PATH_POINT);
        }
        if (!graph.containsVertex(start)) {
            throw new SubwayException(NOT_FOUND_START_PATH_POINT);
        }
        if (!graph.containsVertex(end)) {
            throw new SubwayException(ErrorCode.NOT_FOUND_END_PATH_POINT);
        }
    }

}