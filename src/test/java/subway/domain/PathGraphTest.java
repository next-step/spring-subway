package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PathGraphTest {
    @Test
    @DisplayName("출발역과 도착역을 지정하면 최단 경로를 반환하는 기능")
    void getShortestPath() {
        // given
        Station station1 = new Station("서울역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("상도역");
        Station station4 = new Station("강남역");
        Section section1_2 = new Section(station1, station2, 11);
        Section section1_3 = new Section(station1, station3, 10);
        Section section2_4 = new Section(station2, station4, 10);
        Section section3_4 = new Section(station3, station4, 10);
        PathGraph graph = PathGraph.of(List.of(section1_2, section1_3, section2_4, section3_4));

        // when
        Path path = graph.findShortestPath(station1, station4);

        // then
        assertThat(path.getStations()).containsExactly(station1, station3, station4);
        assertThat(path.getDistance()).isEqualTo(20);
    }

    @Test
    @DisplayName("출발역과 도착역이 같으면 경로 탐색 불가능")
    void getShortestPathSameStations() {
        // given
        Station station1 = new Station("서울역");
        Station station2 = new Station("잠실역");
        Section section1_2 = new Section(station1, station2, 11);
        PathGraph sections = PathGraph.of(List.of(section1_2));

        // when, then
        assertThatCode(() -> sections.findShortestPath(station1, station1))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.PATH_SAME_STATIONS.getMessage());
    }

    @Test
    @DisplayName("sections가 비어 있으면 그래프 생성 불가")
    void emptySectionsError() {
        assertThatCode(() -> PathGraph.of(List.of()))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.PATH_NO_SECTIONS.getMessage());
    }

    @Test
    @DisplayName("출발역이 존재하지만 포함된 구간이 존재하지 않으면 오류")
    void notContainedSourceStation() {
        // given
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "잠실역");
        Station station3 = new Station(3L, "강남역");
        Section section1_2 = new Section(station1, station2, 11);
        PathGraph sections = PathGraph.of(List.of(section1_2));

        // when, then
        assertThatCode(() -> sections.findShortestPath(station3, station2))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.STATION_NOT_CONTAINED.getMessage() + station3.getId());
    }

    @Test
    @DisplayName("도착역이 존재하지만 포함된 구간이 존재하지 않으면 오류")
    void notContainedTargetStation() {
        // given
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "잠실역");
        Station station3 = new Station(3L, "강남역");
        Section section1_2 = new Section(station1, station2, 11);
        PathGraph sections = PathGraph.of(List.of(section1_2));

        // when, then
        assertThatCode(() -> sections.findShortestPath(station1, station3))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.STATION_NOT_CONTAINED.getMessage() + station3.getId());
    }
}
