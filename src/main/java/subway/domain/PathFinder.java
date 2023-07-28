package subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.application.dto.ShortestPath;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

public class PathFinder {

    private final Map<Long, Section> upStationMap = new HashMap<>();
    private final Map<Long, Section> downStationMap = new HashMap<>();

    public PathFinder(List<Section> sections) {
        sections.forEach(section -> {
            upStationMap.put(section.getUpStation().getId(), section);
            downStationMap.put(section.getDownStation().getId(), section);
        });
    }

    public ShortestPath searchShortestPath(long sourceId, long targetId) {
        Station source = getStation(sourceId);
        Station target = getStation(targetId);

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

    private Station getStation(long stationId) {
        if (upStationMap.containsKey(stationId)) {
            return upStationMap.get(stationId).getUpStation();
        }

        if (downStationMap.containsKey(stationId)) {
            return downStationMap.get(stationId).getDownStation();
        }

        throw new IllegalStationsException("존재하지 않는 역 정보입니다.");
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedGraph(long stationId) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
            DefaultWeightedEdge.class);
        addVertexes(graph);
        addStationConnectedSections(stationId, graph);
        addRemainSections(stationId, graph);
        return graph;
    }

    private void addRemainSections(long stationId, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        addConnectedEdges(upStationMap, stationId, graph);
        addConnectedEdges(downStationMap, stationId, graph);
    }

    private void addConnectedEdges(Map<Long, Section> stationMap, long stationId,
        WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        stationMap.keySet().stream()
            .filter(key -> key != stationId)
            .forEach(key -> graph.setEdgeWeight(
                graph.addEdge(stationMap.get(key).getUpStation(),
                    stationMap.get(key).getDownStation()),
                stationMap.get(key).getDistance()));
    }

    private void addStationConnectedSections(long stationId, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        if (upStationMap.containsKey(stationId)) {
            Section section = upStationMap.get(stationId);
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance());
        }

        if (downStationMap.containsKey(stationId)) {
            Section section = downStationMap.get(stationId);
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance());
        }
    }

    private void addVertexes(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        upStationMap.keySet()
            .forEach(key -> {
                graph.addVertex(upStationMap.get(key).getUpStation());
                graph.addVertex(upStationMap.get(key).getDownStation());
            });
    }
}
