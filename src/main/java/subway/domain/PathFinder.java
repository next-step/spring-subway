package subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.ErrorCode;
import subway.exception.SectionException;

public class PathFinder {

    private final DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath;

    public PathFinder(final List<Section> sections) {
        validateNotEmpty(sections);

        this.shortestPath = initGraph(sections);
    }

    private void validateNotEmpty(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SectionException(ErrorCode.AT_LEAST_ONE_SECTION, "경로를 조회할 구간이 존재하지 않습니다.");
        }
    }

    private DijkstraShortestPath<Long, DefaultWeightedEdge> initGraph(final List<Section> sections) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph
                = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        setVertices(graph, sections);
        setEdges(graph, sections);

        return new DijkstraShortestPath<>(graph);
    }

    private void setVertices(final WeightedMultigraph<Long, DefaultWeightedEdge> graph, final List<Section> sections) {
        graph.addVertex(sections.get(0).getUpStation().getId());
        sections.forEach(section -> graph.addVertex(section.getDownStation().getId()));
    }

    private void setEdges(final WeightedMultigraph<Long, DefaultWeightedEdge> graph, final List<Section> sections) {
        sections.forEach(section -> {
            final Long upStationId = section.getUpStation().getId();
            final Long downStationId = section.getDownStation().getId();
            final int distance = section.getDistance().getValue();

            graph.setEdgeWeight(graph.addEdge(upStationId, downStationId), distance);
        });
    }

    public PathFinderResult findShortestPath(final Station source, final Station target) {
        GraphPath<Long, DefaultWeightedEdge> path = shortestPath.getPath(source.getId(), target.getId());

        return new PathFinderResult(path.getVertexList(), (int) path.getWeight());
    }
}
