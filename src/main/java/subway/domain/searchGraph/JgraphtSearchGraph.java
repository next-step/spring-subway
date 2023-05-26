package subway.domain.searchGraph;

import lombok.AccessLevel;
import lombok.Getter;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.springframework.stereotype.Component;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.vo.SubwayPath;

import java.util.ArrayList;
import java.util.List;

/**
 * 최단거리 탐색을 위한 탐색 그래프
 * jgrapht 라이브러리 사용
 */
@Component
public class JgraphtSearchGraph implements SearchGraph{

    /**
     * 최단거리 탐색을 위한 jgrapht 의 그래프 클래스
     * thread-safe
     */
    private final AsSynchronizedGraph<Station, DefaultWeightedEdge> graph;

    public JgraphtSearchGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> nonAsyncGraph
                = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.graph = new AsSynchronizedGraph<>(nonAsyncGraph);
    }

    /**
     * 역을 추가합니다.
     * @param station
     */
    public void addStation(Station station) {
        graph.addVertex(station);
    }

    /**
     * 구간을 추가합니다.
     * @param section
     */
    public void addSection(Section section) {
        Integer distance = section.getDistance().getValue();
        graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation())
                , distance);
    }

    /**
     * 최단경로를 구합니다.
     * @Param 탐색 시작 역
     * @Param 탐색 도착 역
     * @return GraphPath - 경로, 최단거리
     */
    public SubwayPath getShortenPath(Station startStation, Station endStation) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(startStation, endStation);
        return SubwayPath.of(graphPath.getVertexList(), graphPath.getWeight());
    }

    /**
     * 저장된 역을 반환합니다.
     * @return
     */
    public List<Station> getStations() {
        return new ArrayList<>(graph.vertexSet());
    }
}
