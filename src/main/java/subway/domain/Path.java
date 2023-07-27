package subway.domain;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class Path {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph =
            new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public Path(final List<Section> sections) {
        createVertex(sections);
        createEdgeHasWeight(sections);
    }

    public List<Long> findShortestPath(final Long sourceId, final Long targetId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(this.graph);
        return dijkstraShortestPath.getPath(sourceId, targetId).getVertexList();
    }

    public long findShortestPathWeight(final Long sourceId, final Long targetId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(this.graph);
        return (long) dijkstraShortestPath.getPathWeight(sourceId, targetId);
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
