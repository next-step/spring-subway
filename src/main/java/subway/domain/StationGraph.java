package subway.domain;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.SubwayIllegalArgumentException;

import java.util.List;
import java.util.Objects;

public class StationGraph {

    private final Graph<Long, DefaultWeightedEdge> stationGraph
            = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public StationGraph(final DistinctSections sections) {
        validateIsNotEmptySections(sections);

        initGraph(sections);
    }

    private void initGraph(final DistinctSections sections) {
        sections.getSectionIds().forEach(stationGraph::addVertex);

        for (Section section : sections.getSections()) {
            stationGraph.setEdgeWeight(
                    stationGraph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance().getValue()
            );
        }
    }

    private void validateIsNotEmptySections(final DistinctSections sections) {
        if (sections.isEmpty()) {
            throw new SubwayIllegalArgumentException("경로 탐색을 위해 구간 정보가 필요합니다.");
        }
    }

    public List<Long> getShortestPathStationIds(final Long from, final Long to) {
        return getShortestPath(from, to).getVertexList();
    }

    public Distance getShortestPathDistance(final Long from, final Long to) {
        return new Distance(getShortestPath(from, to).getWeight());
    }

    private GraphPath<Long, DefaultWeightedEdge> getShortestPath(final Long from, final Long to) {
        validateFromStationToStationIsNotSame(from, to);
        validateContainsStation(from);
        validateContainsStation(to);

        final GraphPath<Long, DefaultWeightedEdge> shortestPath
                = new DijkstraShortestPath<>(stationGraph).getPath(from, to);
        if (shortestPath == null) {
            throw new SubwayIllegalArgumentException(from + "번역과 " + to + "번역 사이에 경로가 존재하지 않습니다.");
        }

        return shortestPath;
    }

    private void validateFromStationToStationIsNotSame(final Long from, final Long to) {
        if (Objects.equals(from, to)) {
            throw new SubwayIllegalArgumentException("출발역과 도착역이 " + from + "번으로 같아 최단 경로를 구할 수 없습니다.");
        }
    }

    private void validateContainsStation(final Long stationId) {
        if (notContains(stationId)) {
            throw new SubwayIllegalArgumentException(stationId + "번 역이 존재하지 않아 최단 경로를 구할 수 없습니다.");
        }
    }

    private boolean notContains(final Long stationId) {
        return !stationGraph.containsVertex(stationId);
    }
}
