package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Objects;

public final class DijkstraPathManager implements PathManager {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap;

    public DijkstraPathManager(final List<Station> stations, final List<Section> sections) {
        final WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(subwayMap::addVertex);
        sections.forEach(section -> subwayMap.setEdgeWeight(
                subwayMap.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
        ));
        this.subwayMap = subwayMap;
    }

    @Override
    public List<Station> findStationsOfShortestPath(final Station source, final Station target) {
        validateStations(source, target);

        final GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath().getPath(source, target);
        validateShortestPath(shortestPath);
        return shortestPath.getVertexList();
    }

    @Override
    public double findDistanceOfShortestPath(final Station source, final Station target) {
        validateStations(source, target);

        final double distance = dijkstraShortestPath().getPathWeight(source, target);
        validateDistance(distance);
        return distance;
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath() {
        return new DijkstraShortestPath<>(subwayMap);
    }

    private void validateStations(final Station source, final Station target) {
        validateStationExist(source);
        validateStationExist(target);
        validateStationsDuplicate(source, target);
    }

    private void validateStationExist(final Station station) {
        if (!subwayMap.containsVertex(station)) {
            throw new SubwayException(String.format("%s은(는) 존재하지 않는 역입니다.", station.getName()));
        }
    }

    private void validateStationsDuplicate(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new SubwayException("출발역과 도착역은 같을 수 없습니다.");
        }
    }

    private void validateShortestPath(final GraphPath<Station, DefaultWeightedEdge> shortestPath) {
        if (Objects.isNull(shortestPath)) {
            throw new SubwayException("출발역과 도착역을 연결하는 경로가 존재하지 않습니다.");
        }
    }

    private void validateDistance(final double distance) {
        // DijkstraShortestPath에서 경로를 찾을 수 없는 경우에는 POSITIVE_INFINITY를 반환한다.
        if (Double.isInfinite(distance)) {
            throw new SubwayException("출발역과 도착역을 연결하는 경로가 존재하지 않습니다.");
        }
    }
}
