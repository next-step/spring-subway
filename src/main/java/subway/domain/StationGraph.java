package subway.domain;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.exception.SubwayIllegalArgumentException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class StationGraph {

    private final Graph<Long, DefaultWeightedEdge> graph
            = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public StationGraph(final List<Section> sections) {
        validateIsNotEmptySections(sections);

        initVertices(sections);
        initEdges(sections);
    }

    public List<Long> getShortestPathStationIds(final Long from, final Long to) {
        return getShortestPath(from, to).getVertexList();
    }

    public Distance getShortestPathDistance(final Long from, final Long to) {
        return new Distance((int) getShortestPath(from, to).getWeight());
    }

    private void initVertices(final List<Section> sections) {
        final Set<Long> sectionIds = new HashSet<>();
        for (Section section : sections) {
            sectionIds.add(section.getUpStationId());
            sectionIds.add(section.getDownStationId());
        }
        sectionIds.forEach(graph::addVertex);
    }

    private void initEdges(final List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                    section.getDistance().getValue()
            );
        }
    }

    private GraphPath<Long, DefaultWeightedEdge> getShortestPath(final Long from, final Long to) {
        validateFromStationToStationIsNotSame(from, to);
        validateContainsStation(from);
        validateContainsStation(to);

        final GraphPath<Long, DefaultWeightedEdge> shortestPath
                = new DijkstraShortestPath<>(graph).getPath(from, to);
        if (shortestPath == null) {
            throw new SubwayIllegalArgumentException(from + "번역과 " + to + "번역 사이에 경로가 존재하지 않습니다.");
        }

        return shortestPath;
    }

    private void validateIsNotEmptySections(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SubwayIllegalArgumentException("경로 탐색을 위해 구간 정보가 필요합니다.");
        }
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
        return !graph.containsVertex(stationId);
    }
}
