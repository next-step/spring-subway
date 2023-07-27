package subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.util.List;

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
        try {
            return shortestPath.getPath(source, target).getVertexList();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IncorrectRequestException(
                    ErrorCode.NO_CONNECTED_PATH,
                    String.format("출발역: %s, 도착역: %s", source.getName(), target.getName())
            );
        }
    }

    public Distance getDistance(Station source, Station target) {
        return new Distance((int) shortestPath.getPathWeight(source, target));
    }

}
