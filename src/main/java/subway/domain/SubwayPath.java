package subway.domain;

import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.dto.ShortestSubwayPath;

public class SubwayPath {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public SubwayPath(List<LineSections> subway) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        subway.forEach(this::inputLineSectionsInGraph);
    }

    private void inputLineSectionsInGraph(LineSections lineSections) {
        lineSections.getAllStations().forEach(graph::addVertex);
        lineSections.getSections().getValues().forEach(this::inputSectionInGraph);
    }

    private void inputSectionInGraph(Section section) {
        this.graph.setEdgeWeight(this.graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public ShortestSubwayPath calculateShortestPath(Station sourceStation, Station destinationStation) {
        validateCanReachDestinationStation(sourceStation, destinationStation);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            graph);

        return new ShortestSubwayPath(dijkstraShortestPath.getPath(sourceStation, destinationStation).getVertexList(),
            dijkstraShortestPath.getPath(sourceStation, destinationStation).getWeight());
    }

    private void validateCanReachDestinationStation(Station sourceStation,
        Station destinationStation) {
        if (new DijkstraShortestPath<>(graph).getPath(sourceStation, destinationStation) == null) {
            throw new IllegalArgumentException(
                "갈 수 있는 경로가 존재하지 않습니다. 출발역: " + sourceStation + " 도착역: " + destinationStation);
        }
    }

}
