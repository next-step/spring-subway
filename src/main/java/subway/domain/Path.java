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

    public Path(final List<Section> sections) {
        createVertex(sections);
        createEdgeHasWeight(sections);
    }

    public List<Long> findShortestPath(final Long sourceId, final Long targetId) {
        final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(this.graph);
        final GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceId, targetId);
        if (Objects.isNull(path)) {
            throw new SubwayException("출발역과 도착역이 연결되어 있지 않습니다. 출발역 ID : " + sourceId + " 도착역 ID : " + targetId);
        }
        return path.getVertexList();
    }

    public long findShortestPathWeight(final Long sourceId, final Long targetId) {
        final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(this.graph);
        final double pathWeight = dijkstraShortestPath.getPathWeight(sourceId, targetId);
        if (Double.isFinite(pathWeight)) {
            throw new SubwayException("출발역과 도착역이 연결되어 있지 않습니다. 출발역 ID : " + sourceId + " 도착역 ID : " + targetId);
        }
        return (long) pathWeight;
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
