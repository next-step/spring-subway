package subway.domain;

import java.util.List;
import java.util.Objects;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.SubwayException;

public class Path {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private GraphPath<Long, DefaultWeightedEdge> shortestPath;

    public Path(final List<Section> sections) {
        createVertex(sections);
        createEdgeHasWeight(sections);
    }

    public List<Long> findShortestPathVertices(final Long sourceId, final Long targetId) {
        if (this.shortestPath == null) {
            this.shortestPath = getShortestPath(sourceId, targetId);
        }

        return this.shortestPath.getVertexList();
    }

    public long findShortestPathWeight(final Long sourceId, final Long targetId) {
        if (this.shortestPath == null) {
            this.shortestPath = getShortestPath(sourceId, targetId);
        }

        return (long) this.shortestPath.getWeight();
    }

    public boolean isPathHasVertex(final Long sourceId, final Long targetId) {
        return this.graph.containsVertex(sourceId) && this.graph.containsVertex(targetId);
    }

    private GraphPath<Long, DefaultWeightedEdge> getShortestPath(final Long sourceId, final Long targetId) {
        final GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath<>(this.graph)
                .getPath(sourceId, targetId);

        if (Objects.isNull(path)) {
            throw new SubwayException("출발역과 도착역이 연결되어 있지 않습니다. 출발역 ID : " + sourceId + " 도착역 ID : " + targetId);
        }

        return path;
    }

    private void createVertex(final List<Section> sections) {
        for (Section section : sections) {
            this.graph.addVertex(section.getUpStationId());
            this.graph.addVertex(section.getDownStationId());
        }
    }

    private void createEdgeHasWeight(final List<Section> sections) {
        for (Section section : sections) {
            this.graph.setEdgeWeight(
                    this.graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance()
            );
        }
    }
}
