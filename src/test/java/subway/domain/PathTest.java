package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    @DisplayName("경로 도메인이 성공적으로 생성된다.")
    void create() {
        final List<Section> sections = List.of(
                new Section(1L, 1L, 2L, 1L),
                new Section(1L, 2L, 3L, 2L),
                new Section(1L, 3L, 4L, 3L)
        );

        assertDoesNotThrow(() -> new Path(sections));
    }

    @Test
    @DisplayName("최단 경로를 찾는다.")
    void findShortestPath() {
        final Long sourceId = 1L;
        final Long targetId = 4L;
        final List<Section> sections = List.of(
                new Section(1L, sourceId, 2L, 1L),
                new Section(1L, 2L, 3L, 2L),
                new Section(1L, 3L, targetId, 3L)
        );
        final Path path = new Path(sections);

        List<Long> shortestPathStationIds = path.findShortestPathVertices(sourceId, targetId);

        assertThat(shortestPathStationIds).containsExactly(sourceId, 2L, 3L, targetId);
    }

    @Test
    @DisplayName("최단 경로의 비용을 찾는다.")
    void findShortestPathWeight() {
        final Long sourceId = 1L;
        final Long targetId = 4L;
        final List<Section> sections = List.of(
                new Section(1L, sourceId, 2L, 1L),
                new Section(1L, 2L, 3L, 2L),
                new Section(1L, 3L, targetId, 3L)
        );
        final Path path = new Path(sections);

        long shortestPathWeight = path.findShortestPathWeight(sourceId, targetId);

        assertThat(shortestPathWeight).isEqualTo(6);
    }

    @Test
    @DisplayName("경로에 시작 역과 도착 역을 둘 다 포함하고 있는지 확인한다.")
    void isPathHasVertex() {
        final Long sourceId = 1L;
        final Long targetId = 4L;
        final List<Section> sections = List.of(
                new Section(1L, sourceId, 2L, 1L),
                new Section(1L, 2L, 3L, 2L),
                new Section(1L, 3L, targetId, 3L)
        );
        final Path path = new Path(sections);

        assertThat(path.isPathHasVertex(sourceId, targetId)).isTrue();
        assertThat(path.isPathHasVertex(sourceId, 5L)).isFalse();
        assertThat(path.isPathHasVertex(5L, targetId)).isFalse();
        assertThat(path.isPathHasVertex(5L, 6L)).isFalse();
    }
}
