package subway.domain;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.shortestpath.ShortestPathSameStationException;

public class ShortestPath {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public ShortestPath(List<Station> stations, List<Section> sections) {
        this.dijkstraShortestPath = new DijkstraShortestPath<>(generateGraph(stations, sections));
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> generateGraph(List<Station> stations,
        List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section ->
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        return graph;
    }

    public List<Station> getPaths(Station sourceStation, Station targetStation) {
        validateSameStation(sourceStation, targetStation);
        return dijkstraShortestPath.getPath(sourceStation, targetStation)
            .getVertexList();
    }

    private void validateSameStation(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new ShortestPathSameStationException();
        }
    }

    public int getDistance(Station sourceStation, Station targetStation) {
        validateSameStation(sourceStation, targetStation);
        return (int) dijkstraShortestPath.getPath(sourceStation, targetStation)
            .getWeight();
    }
}
