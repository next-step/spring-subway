package subway.utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.domain.PathFinder;
import subway.domain.Section;
import subway.domain.Station;

public class Jgrapht implements PathFinder {

    private final GraphPath<Station, DefaultWeightedEdge> shortestPath;

    public Jgrapht(
        List<Section> sections,
        Station departureStation,
        Station destinationStation
    ) {
        validateStation(departureStation, destinationStation);
        shortestPath = findShortestPath(sections, departureStation, destinationStation);
    }

    private void validateStation(Station departureStation, Station destinationStation) {
        if (departureStation.equals(destinationStation)) {
            throw new IllegalArgumentException("출발역과 도착역이 일치합니다.");
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> findShortestPath(
        List<Section> sections,
        Station departureStation,
        Station destinationStation
    ) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = setWeightedMultigraph(sections);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstra =
            new DijkstraShortestPath<>(graph);
        try {
            return dijkstra.getPath(
                departureStation,
                destinationStation
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException("연결된 노선이 없습니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> setWeightedMultigraph(
        List<Section> sections
    ) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph =
            new WeightedMultigraph(DefaultWeightedEdge.class);

        addVertex(graph, sections);
        setEdgeWeight(graph, sections);

        return graph;
    }

    private void addVertex(
        WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        List<Section> sections
    ) {
        findStations(sections).forEach(graph::addVertex);
    }

    private void setEdgeWeight(
        WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        List<Section> sections
    ) {
        for (Section section : sections) {
            graph.setEdgeWeight(
                graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation()
                ),
                section.getDistance().getDistance()
            );
        }
    }

    private Set<Station> findStations(List<Section> sections) {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public List<Station> findShortestStations() {
        return shortestPath.getVertexList();
    }

    @Override
    public Double findShortestDistance() {
        return shortestPath.getWeight();
    }

}
