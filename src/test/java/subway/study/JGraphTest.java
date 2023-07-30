package subway.study;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static subway.fixture.SectionFixture.DEFAULT_DISTANCE;
import static subway.fixture.StationFixture.*;

@DisplayName("JGraph 라이브러리 테스트")
class JGraphTest {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath;

    JGraphTest() {
        /*
          <지하철 노선도>
          범계 -10- 경마공원 -10- 사당 -10- 신용산
                     |         |
                     50       10
                     ㄴ ------ 강남 -10- 잠실
          여의도 -10- 노량진
         */
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        graph.addVertex(범계역());
        graph.addVertex(경마공원역());
        graph.addVertex(사당역());
        graph.addVertex(신용산역());
        graph.addVertex(강남역());
        graph.addVertex(잠실역());
        graph.addVertex(여의도역());
        graph.addVertex(노량진역());

        graph.setEdgeWeight(graph.addEdge(범계역(), 경마공원역()), DEFAULT_DISTANCE);
        graph.setEdgeWeight(graph.addEdge(경마공원역(), 사당역()), DEFAULT_DISTANCE);
        graph.setEdgeWeight(graph.addEdge(사당역(), 신용산역()), DEFAULT_DISTANCE);
        graph.setEdgeWeight(graph.addEdge(경마공원역(), 강남역()), DEFAULT_DISTANCE * 5);
        graph.setEdgeWeight(graph.addEdge(사당역(), 강남역()), DEFAULT_DISTANCE);
        graph.setEdgeWeight(graph.addEdge(강남역(), 잠실역()), DEFAULT_DISTANCE);
        graph.setEdgeWeight(graph.addEdge(여의도역(), 노량진역()), DEFAULT_DISTANCE);

        this.shortestPath = new DijkstraShortestPath<>(graph);
    }

    @DisplayName("출발역과 도착역으로 최단 경로를 반환하는 데 성공한다.")
    @Test
    void getShortestPath() {
        // when
        final List<Station> 최단_경로 = shortestPath.getPath(범계역(), 잠실역()).getVertexList();

        // then
        assertThat(최단_경로).containsExactly(범계역(), 경마공원역(), 사당역(), 강남역(), 잠실역());
    }

    @DisplayName("출발역과 도착역이 같으면 최단 경로는 해당 역이다.")
    @Test
    void getShortestPathWithSameStations() {
        // when
        final List<Station> 최단_경로 = shortestPath.getPath(범계역(), 범계역()).getVertexList();

        // then
        assertThat(최단_경로).containsExactly(범계역());
    }

    @DisplayName("출발역 또는 도착역이 존재하지 않으면 최단 경로를 반환하는 데 실패한다.")
    @Test
    void getShortestPathWithStationNoExist() {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> shortestPath.getPath(범계역(), 첫번째역()).getVertexList());
        assertThatIllegalArgumentException().isThrownBy(() -> shortestPath.getPath(첫번째역(), 범계역()).getVertexList());
    }

    @DisplayName("출발역과 도착역을 연결하는 경로가 존재하지 않으면 최단 경로를 반환하는 데 실패한다.")
    @Test
    void getShortestPathWithStationsNotConnected() {
        // when & then
        assertThatNullPointerException().isThrownBy(() -> shortestPath.getPath(범계역(), 여의도역()).getVertexList());
    }

    @DisplayName("출발역과 도착역을 연결하는 경로가 존재하지 않으면 최단 거리를 반환하는 데 실패한다.")
    @Test
    void getShortestDistanceWithStationsNotConnected() {
        // when & then
        assertThat(shortestPath.getPathWeight(범계역(), 여의도역())).isInfinite();
    }
}
