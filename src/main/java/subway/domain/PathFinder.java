package subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
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

    public double calculateShortestDistance(long sourceId, long targetId) {
        Station source = getStation(sourceId);
        Station target = getStation(targetId);

        validateSourceAndTarget(source, target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            createWeightedGraph(sourceId));

        return dijkstraShortestPath.getPath(source, target).getWeight();
    }

    public List<Station> searchShortestPath(long sourceId, long targetId) {
        Station source = getStation(sourceId);
        Station target = getStation(targetId);

        validateSourceAndTarget(source, target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            createWeightedGraph(sourceId));

        return Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
            .map(GraphPath::getVertexList)
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

        upStationMap.keySet()
            .forEach(key -> graph.addVertex(upStationMap.get(key).getUpStation()));
        downStationMap.keySet()
            .forEach(key -> graph.addVertex(downStationMap.get(key).getDownStation()));

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

        upStationMap.keySet().stream()
            .filter(key -> key != stationId)
            .forEach(key -> graph.setEdgeWeight(
                graph.addEdge(upStationMap.get(key).getUpStation(),
                    upStationMap.get(key).getDownStation()),
                upStationMap.get(key).getDistance()));

        downStationMap.keySet().stream()
            .filter(key -> key != stationId)
            .forEach(key -> graph.setEdgeWeight(
                graph.addEdge(downStationMap.get(key).getUpStation(),
                    downStationMap.get(key).getDownStation()),
                downStationMap.get(key).getDistance()));

        return graph;
    }
}
