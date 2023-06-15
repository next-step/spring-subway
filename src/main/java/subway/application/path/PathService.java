package subway.application.path;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    static final WeightedPseudograph<Long, DefaultWeightedEdge> GRAPH = new WeightedPseudograph<>(DefaultWeightedEdge.class);

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public PathService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostConstruct
    void addExistingDataToGraph() {
        List<Station> stations = stationDao.findAll();
        for (Station station : stations) {
            addVertex(station.getId());
        }

        List<Section> sections = sectionDao.findAll();
        for (Section section : sections) {
            addEdge(section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
        }
    }

    public PathResponse findShortestPath(Long departureStationId, Long arrivalStationId) {
        validateStationsAreNotSame(departureStationId, arrivalStationId);

        GraphPath<Long, DefaultWeightedEdge> path = getPath(departureStationId, arrivalStationId);
        List<StationResponse> stationResponses = path.getVertexList().stream()
                .map(stationId -> stationDao.findById(stationId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Station 입니다.")))
                .map(StationResponse::of)
                .collect(Collectors.toList());
        int totalDistance = (int) path.getWeight();

        return new PathResponse(stationResponses, totalDistance, PathPrice.calculate(totalDistance));
    }

    private void validateStationsAreNotSame(Long departureStationId, Long arrivalStationId) {
        if (departureStationId.equals(arrivalStationId)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }
    }

    private GraphPath<Long, DefaultWeightedEdge> getPath(Long departureStationId, Long arrivalStationId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(GRAPH);

        GraphPath<Long, DefaultWeightedEdge> path;
        try {
            path = dijkstra.getPath(departureStationId, arrivalStationId);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("출발역 또는 도착역이 존재하지 않습니다.");
        }

        if (path == null) {
            throw new IllegalArgumentException("출발역과 도착역 사이에 경로가 존재하지 않습니다.");
        }

        return path;
    }

    public void addVertex(Long vertex) {
        GRAPH.addVertex(vertex);
    }

    public void removeVertex(Long vertex) {
        GRAPH.removeVertex(vertex);
    }

    public void addEdge(Long startVertex, Long endVertex, int weight) {
        GRAPH.setEdgeWeight(GRAPH.addEdge(startVertex, endVertex), weight);
    }

    public void removeEdge(Long startVertex, Long endVertex) {
        GRAPH.removeEdge(startVertex, endVertex);
    }

}
