package subway.domain;

import java.util.List;
import java.util.Optional;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.PathNotFoundException;
import subway.exception.SameSourceAndTargetException;

public class PathGraph {

    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    public PathGraph(final List<Section> allSections, final Station source, final Station target) {
        validateStations(source, target);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = toGraph(allSections);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath<>(graph);
        this.graphPath = Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
                .orElseThrow(() -> new PathNotFoundException(source.getId(), target.getId()));
    }

    private void validateStations(Station source, Station target) {
        if (source.getId().equals(target.getId())) {
            throw new SameSourceAndTargetException();
        }
    }

    private static WeightedMultigraph<Station, DefaultWeightedEdge> toGraph(
            List<Section> allSections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allSections.forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance());
        });
        return graph;
    }

    public List<Station> findRoute() {
        return graphPath.getVertexList();
    }

    public Long findDistance() {
        return Math.round(graphPath.getWeight());
    }
}
