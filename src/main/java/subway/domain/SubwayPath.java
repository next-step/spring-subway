package subway.domain;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SubwayPath {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public SubwayPath(List<LineSections> multiLineSections) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        multiLineSections.forEach(lineSections -> {
            lineSections.getAllStations().forEach(graph::addVertex);
            lineSections.getSections().getValues().forEach(section ->
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        });
    }

    public List<Station> getShortestPath(Station sourceStation, Station destination) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceStation, destination).getVertexList();
    }

    public double getShortestDistance(Station sourceStation, Station destination) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceStation, destination).getWeight();
    }
}
