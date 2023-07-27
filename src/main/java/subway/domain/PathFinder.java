package subway.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

    private final GraphPath<Station, DefaultWeightedEdge> shortestPath;

    public PathFinder(List<Section> sections, Station departureStation, Station destinationStation) {
        shortestPath = findShortestPath(sections, departureStation, destinationStation);
    }

    private GraphPath<Station, DefaultWeightedEdge> findShortestPath(
        List<Section> sections,
        Station departureStation,
        Station destinationStation
    ) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = setWeightedMultigraph(sections);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        return dijkstra.getPath(
            departureStation,
            destinationStation
        );
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

    private void setEdgeWeight(
        WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        List<Section> sections
    ) {
        for (Section section: sections) {
            graph.setEdgeWeight(
                graph.addEdge(
                    section.getUpStation(),
                    section.getDownStation()
                ),
                section.getDistance().getDistance()
            );
        }
    }

    private void addVertex(
        WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        List<Section> sections
    ) {
        findStations(sections).stream()
            .forEach(graph::addVertex);
    }

    private Set<Station> findStations(List<Section> sections) {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toUnmodifiableSet());
    }

    public List<Station> findShortestStations() {
        // Main Logic
        // 최단 경로 출력
//        System.out.println("Shortest path: " + shortestPath.getVertexList());
//        System.out.println("Shortest path weight: " + shortestPath.getWeight());

        // Test
//        Station station1 = new Station(5L, "교대역");
//        Station station2 = new Station(7L, "남부터미널역");
//        Station station3 = new Station(8L, "양재역");
//        List<Station> stations = List.of(station1, station2, station3);
        return shortestPath.getVertexList();
    }

    public Double findMinimumDistance() {
        return shortestPath.getWeight();
    }
}
