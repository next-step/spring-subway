package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayIllegalArgumentException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StationGraphTest {

    List<Long> stations = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    List<Section> sections = List.of(
            new Section(1L, 1L, 1L, 2L, new Distance(16)),
            new Section(2L, 1L, 2L, 1L, new Distance(1)),
            new Section(3L, 1L, 1L, 3L, new Distance(9)),
            new Section(4L, 1L, 1L, 4L, new Distance(35)),
            new Section(5L, 2L, 2L, 4L, new Distance(12)),
            new Section(6L, 2L, 2L, 5L, new Distance(25)),
            new Section(7L, 3L, 3L, 4L, new Distance(15)),
            new Section(8L, 3L, 3L, 6L, new Distance(22)),
            new Section(9L, 3L, 4L, 5L, new Distance(14)),
            new Section(10L, 3L, 4L, 6L, new Distance(17)),
            new Section(11L, 3L, 4L, 7L, new Distance(19)),
            new Section(12L, 3L, 5L, 7L, new Distance(8)),
            new Section(13L, 3L, 6L, 7L, new Distance(14)),
            new Section(14L, 3L, 8L, 9L, new Distance(777))
    );
    StationGraph stationGraph;

    @BeforeEach
    void setUp() {
        stationGraph = new StationGraph(stations, sections);
    }

    @Test
    @DisplayName("경로를 생성할 수 있다.")
    void create() {
        /* given */

        /* when & then */
        assertDoesNotThrow(() -> new StationGraph(stations, sections));
    }

    @Test
    @DisplayName("역이 존재하지 않는 경우 경로 생성에 실패한다.")
    void creatFailWithNullOrEmptyStations() {
        /* given */


        /* when & then */
        assertThatThrownBy(() -> new StationGraph(null, sections))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("경로 탐색을 위해 역 정보가 필요합니다.");
        assertThatThrownBy(() -> new StationGraph(Collections.emptyList(), sections))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("경로 탐색을 위해 역 정보가 필요합니다.");
    }

    @Test
    @DisplayName("구간이 존재하지 않는 경우 생성에 실패한다.")
    void creatFailWithNullOrEmptySections() {
        /* given */


        /* when & then */
        assertThatThrownBy(() -> new StationGraph(stations, null))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("경로 탐색을 위해 구간 정보가 필요합니다.");
        assertThatThrownBy(() -> new StationGraph(stations, Collections.emptyList()))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("경로 탐색을 위해 구간 정보가 필요합니다.");
    }

    @Test
    @DisplayName("출발역부터 도착역까지의 최단 경로의 역들을 구한다.")
    void getPathStationIds() {
        /* given */
        final Long fromStationId = 1L;
        final Long toStationId = 7L;

        /* when */
        final List<Long> pathStationIds = stationGraph.getPathStationIds(fromStationId, toStationId);

        /* then */
        assertThat(pathStationIds).isEqualTo(List.of(1L, 2L, 4L, 7L));
    }

    @Test
    @DisplayName("출발역 또는 도착역이 그래프에 존재하지 않는 경우 최단 경로의 역들을 구할 때 SubwayIllegalException을 던진다.")
    void getPathStationIdsFailWithNotExistStation() {
        /* given */
        final Long existStationId = 1L;
        final Long notExistStationId = 54_321L;

        /* when & then */
        assertThatThrownBy(() -> stationGraph.getPathStationIds(notExistStationId, existStationId))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("54321번 역이 존재하지 않아 최단 경로를 구할 수 없습니다.");
    }

    @Test
    @DisplayName("출발역부터 도착역까지의 최단 경로의 길이 합을 구한다")
    void getShortestPathDistance() {
        /* given */


        /* when */
        final Distance shortestPathDistance = stationGraph.getShortestPathDistance(1L, 7L);

        /* then */
        assertThat(shortestPathDistance).isEqualTo(new Distance(32));
    }

    @Test
    @DisplayName("출발역 또는 도착역이 그래프에 존재하지 않는 경우 최단 경로의 길이를 구할 때 SubwayIllegalException을 던진다.")
    void getShortestPathDistanceFailWithNotExistStation() {
        /* given */
        final Long existStationId = 1L;
        final Long notExistStationId = 54_321L;

        /* when & then */
        assertThatThrownBy(() -> stationGraph.getShortestPathDistance(notExistStationId, existStationId))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("54321번 역이 존재하지 않아 최단 경로를 구할 수 없습니다.");
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 최단 경로의 길이를 구할 때 SubwayIllegalException을 던진다.")
    void getShortestPathDistanceWithSameFromStationIdToStationId() {
        /* given */
        final Long stationId = 1L;

        /* when */
        assertThatThrownBy(() -> stationGraph.getShortestPathDistance(stationId, stationId))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 1번으로 같아 최단 경로를 구할 수 없습니다.");
    }

    @Test
    @DisplayName("출발역과 도착역이 연결되어 있지 않을 경우 최단 경로를 구할 때 SubwayIllegalException을 던진다.")
    void getShortestPathDistanceWithDoesNotConnectedStation() {
        /* given */
        final Long doesNotConnectedFrom = 1L;
        final Long doesNotConnectedTo = 9L;

        /* when & then */
        assertThatThrownBy(() -> stationGraph.getShortestPathDistance(doesNotConnectedFrom, doesNotConnectedTo))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("1번역과 9번역 사이에 경로가 존재하지 않습니다.");
    }
}
