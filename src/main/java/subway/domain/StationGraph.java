package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.util.List;
import java.util.Optional;

public class StationGraph {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath;

    public StationGraph(List<Sections> allSections) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addAllSections(allSections);
        this.shortestPath = new DijkstraShortestPath<>(graph);
    }

    private void addAllSections(List<Sections> allSections) {
        addAllStations(allSections);
        addAllEdges(allSections);
    }

    private void addAllStations(List<Sections> allSections) {
        allSections.stream()
                .map(Sections::toStations)
                .forEach(stations -> stations.forEach(graph::addVertex));
    }

    private void addAllEdges(List<Sections> allSections) {
        allSections.stream()
                .map(Sections::getSections)
                .forEach(this::addSections);
    }

    private void addSections(List<Section> sections) {
        for (Section section : sections) {
            DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance().getValue());
        }
    }

    public List<Station> getPath(Station source, Station target) {
        GraphPath<Station, DefaultWeightedEdge> path = getPathOptional(source, target)
                .orElseThrow(() -> notConnectedException(source, target));
        return path.getVertexList();
    }

    private Optional<GraphPath<Station, DefaultWeightedEdge>> getPathOptional(Station source, Station target) {
        try {
            return Optional.ofNullable(shortestPath.getPath(source, target));
        } catch (IllegalArgumentException e) {
            throw notConnectedException(source, target);
        }
    }

    public Distance getDistance(Station source, Station target) {
        double distance = shortestPath.getPathWeight(source, target);
        if (distance == Double.POSITIVE_INFINITY) {
            throw notConnectedException(source, target);
        }
        return new Distance((int) distance);
    }

    private IncorrectRequestException notConnectedException(Station source, Station target) {
        return new IncorrectRequestException(
                ErrorCode.NO_CONNECTED_PATH,
                String.format("출발역: %s, 도착역: %s", source.getName(), target.getName())
        );
    }

}
