package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.fixture.SectionFixture.*;
import static subway.fixture.StationFixture.*;

class PathManagerTest {

    @DisplayName("PathManager를 생성하는 데 성공한다.")
    @Test
    void createPathManager() {
        // given
        final List<Station> stations = List.of();
        final List<Section> sections = List.of();

        // when & then
        assertThatNoException().isThrownBy(() -> PathManager.create(stations, sections));
    }

    @DisplayName("출발역과 도착역으로 최단 경로를 찾는 데 성공한다.")
    @Test
    void findShortestPath() {
        // given
        final PathManager pathManager = pathManager();

        // when
        final List<Station> 범계역_잠실역_최단경로 = pathManager.findStationsOfShortestPath(범계역(), 잠실역());

        // then
        assertThat(범계역_잠실역_최단경로).containsExactly(
                범계역(), 경마공원역(), 사당역(), 강남역(), 잠실역()
        );
    }

    @DisplayName("출발역 또는 도착역이 존재하지 않아 최단 경로를 찾는 데 실패한다.")
    @Test
    void findShortestPathWithStationNoExist() {
        // given
        final PathManager pathManager = pathManager();

        // when & then
        assertThatThrownBy(() -> pathManager.findStationsOfShortestPath(첫번째역(), 범계역()))
                .hasMessage(첫번째역().getName()+ "은(는) 존재하지 않는 역입니다.")
                .isInstanceOf(SubwayException.class);
        assertThatThrownBy(() -> pathManager.findStationsOfShortestPath(범계역(), 두번째역()))
                .hasMessage(두번째역().getName()+ "은(는) 존재하지 않는 역입니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("출발역과 도착역이 같아 최단 경로를 찾는 데 실패한다.")
    @Test
    void findShortestPathWithSameStations() {
        // given
        final PathManager pathManager = pathManager();

        // when & then
        assertThatThrownBy(() -> pathManager.findStationsOfShortestPath(범계역(), 범계역()))
                .hasMessage("출발역과 도착역은 같을 수 없습니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("출발역과 도착역을 연결하는 경로가 없어 최단 경로를 찾는 데 실패한다.")
    @Test
    void findShortestPathWithNotConnectedStations() {
        // given
        final PathManager pathManager = pathManager();

        // when & then
        assertThatThrownBy(() -> pathManager.findStationsOfShortestPath(범계역(), 여의도역()))
                .hasMessage("출발역과 도착역을 연결하는 경로가 존재하지 않습니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("출발역과 도착역으로 최단 거리를 찾는 데 성공한다.")
    @Test
    void findShortestDistance() {
        // given
        final PathManager pathManager = pathManager();

        // when
        final double 범계역_잠실역_최단거리 = pathManager.findDistanceOfShortestPath(범계역(), 잠실역());

        // then
        assertThat(범계역_잠실역_최단거리).isEqualTo(DEFAULT_DISTANCE * 4);
    }

    @DisplayName("출발역과 도착역을 연결하는 경로가 없어 최단 거리를 찾는 데 실패한다.")
    @Test
    void findDistanceWithNotConnectedStations() {
        // given
        final PathManager pathManager = pathManager();

        // when & then
        assertThatThrownBy(() -> pathManager.findDistanceOfShortestPath(범계역(), 여의도역()))
                .hasMessage("출발역과 도착역을 연결하는 경로가 존재하지 않습니다.")
                .isInstanceOf(SubwayException.class);
    }

    private PathManager pathManager() {
        /*
          <지하철 노선도>
          범계 -10- 경마공원 -10- 사당 -10- 신용산
                     |         |
                     50       10
                     ㄴ ------ 강남 -10- 잠실
          여의도 -10- 노량진
         */

        final List<Station> stations = List.of(
                범계역(), 경마공원역(), 사당역(), 신용산역(), 강남역(), 잠실역(), 여의도역(), 노량진역()
        );

        final List<Section> sections = List.of(
                범계역_경마공원역_구간(DEFAULT_DISTANCE),
                경마공원역_사당역_구간(DEFAULT_DISTANCE),
                사당역_신용산역_구간(DEFAULT_DISTANCE),
                경마공원역_강남역_구간(DEFAULT_DISTANCE * 5),
                사당역_강남역_구간(DEFAULT_DISTANCE),
                강남역_잠실역_구간(DEFAULT_DISTANCE),
                여의도역_노량진역_구간(DEFAULT_DISTANCE)
        );

        return PathManager.create(stations, sections);
    }
}
