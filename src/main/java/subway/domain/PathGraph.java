package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Set;

import static subway.exception.ErrorCode.*;

public class PathGraph {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private GraphPath<Station, DefaultWeightedEdge> path;

    private PathGraph(final WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        final GraphPath<Station, DefaultWeightedEdge> path) {
        this.graph = graph;
        this.path = path;
    }

    public PathGraph(final Sections sections) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initializeVertex(sections.getStationsCache());
        initializeEdges(sections.getSections());
    }

    private void initializeEdges(final List<Section> edges) {
        edges.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
            )
        );
    }

    private void initializeVertex(final Set<Station> vertex) {
        vertex.forEach(graph::addVertex);
    }


    public PathGraph createPath(final Station start, final Station end) {
        validatePathConnect(start, end);

        GraphPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph)
            .getPath(start, end);
        if (path == null) {
            throw new SubwayException(NOT_CONNECTED_BETWEEN_START_AND_END_PATH);
        }
        return new PathGraph(graph, path);
    }

    private void validatePathConnect(final Station start, final Station end) {
        if (start.equals(end)) {
            throw new SubwayException(SAME_START_END_PATH_POINT);
        }
        if (notContainsVertex(start)) {
            throw new SubwayException(NOT_FOUND_START_PATH_POINT);
        }
        if (notContainsVertex(end)) {
            throw new SubwayException(ErrorCode.NOT_FOUND_END_PATH_POINT);
        }
    }

    public boolean notContainsVertex(final Station station) {
        return !graph.containsVertex(station);
    }

    public long getDistance() {
        return (long) path.getWeight();
    }

    public List<Station> getStations() {
        return path.getVertexList();
    }
}
