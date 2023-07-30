package subway.domain;

import java.util.List;
import java.util.Optional;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.application.dto.ShortestPath;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

public class PathFinder {

    private final ConnectedSection connectedSection;

    public PathFinder(List<Section> sections) {
        connectedSection = new ConnectedSection(sections);
    }

    public ShortestPath searchShortestPath(long sourceId, long targetId) {
        Station source = connectedSection.getStation(sourceId);
        Station target = connectedSection.getStation(targetId);

        validateSourceAndTarget(source, target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            createWeightedGraph(sourceId));

        return Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
            .map(graphPath -> new ShortestPath(graphPath.getWeight(), graphPath.getVertexList()))
            .orElseThrow(() -> new IllegalSectionException("출발역과 도착역이 연결되어 있지 않습니다."));
    }

    private void validateSourceAndTarget(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalStationsException("출발역과 도착역은 달라야 합니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedGraph(long stationId) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
            DefaultWeightedEdge.class);
        addVertexes(graph);
        addStationConnectedSections(stationId, graph);
        addRemainSections(stationId, graph);
        return graph;
    }

    private void addRemainSections(long stationId,
        WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        connectedSection.getAllSections()
            .stream()
            .filter(section -> !section.isContainStation(stationId))
            .forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()));
    }

    private void addStationConnectedSections(long stationId,
        WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        connectedSection.getConnectedSection(stationId)
            .forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()));
    }

    private void addVertexes(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        connectedSection.getAllStations()
            .forEach(graph::addVertex);
    }
}
