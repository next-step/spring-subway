package subway.application.path;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 조회 관련 기능")
@Sql(value = "classpath:/path-testdata.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class PathServiceTest {

    @Autowired
    private PathService pathService;

    @DisplayName("두 역 사이의 최단 경로를 찾는다")
    @MethodSource("subway.application.path.FindShortestPathParam#findShortestPathSource")
    @ParameterizedTest(name = "{3}")
    void findShortestPath(long departureStationId, long arrivalStationId, PathResponse expectedPathResponse, String displayName) {
        // given
        pathService.addExistingDataToGraph();

        // when
        PathResponse pathResponse = pathService.findShortestPath(departureStationId, arrivalStationId);

        // then
        assertThat(pathResponse)
                .usingRecursiveComparison()
                .ignoringFields("path")
                .isEqualTo(expectedPathResponse);

        List<Long> stationIds = pathResponse.getPath().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(pathResponse.getPath())
                .flatExtracting(StationResponse::getId).isEqualTo(stationIds);
    }

    @DisplayName("동일한 역 사이의 최단 경로를 조회한다")
    @Test
    void findShortestPathWithSameStation() {
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 역 사이의 최단 경로를 조회한다")
    @Test
    void findShortestPathWithNonExistenceStation() {
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 100L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("경로가 존재하지 않는 역 사이의 최단 경로를 조회한다")
    @Test
    void findShortestPathWithNonExistencePath() {
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 17L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("그래프에 정점(Station)을 추가한다")
    @Test
    void addVertex() {
        // when
        pathService.addVertex(100L);

        // then
        assertThat(PathService.GRAPH.containsVertex(100L)).isTrue();
    }

    @DisplayName("그래프에서 정점(Station)을 제거한다")
    @Test
    void removeVertex() {
        // when
        pathService.removeVertex(1L);

        // then
        assertThat(PathService.GRAPH.containsVertex(1L)).isFalse();
    }

    @DisplayName("그래프에 간선(Section)을 추가한다")
    @Test
    void addEdge() {
        // given
        pathService.addVertex(100L);
        pathService.addVertex(101L);

        // when
        pathService.addEdge(100L, 101L, 10);

        // then
        DefaultWeightedEdge edge = PathService.GRAPH.getEdge(100L, 101L);

        assertThat(edge).isNotNull();
        assertThat(PathService.GRAPH.getEdgeWeight(edge)).isEqualTo(10);
    }

    @DisplayName("그래프에서 간선(Section)을 제거한다")
    @Test
    void removeEdge() {
        // when
        pathService.removeEdge(1L, 2L);

        // then
        assertThat(PathService.GRAPH.getEdge(1L, 2L)).isNull();
    }

}
