package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;
import subway.ui.dto.PathResponse;

@DisplayName("지하철 경로 검색")
class PathFinderTest {

    @Test
    @DisplayName("PathFinder 를 생성한다.")
    void createPathFinder_noException() {
        // given
        List<Section> sections = createInitialSections();

        // when & then
        assertThatNoException()
            .isThrownBy(() -> new PathFinder(sections));
    }

    @Test
    @DisplayName("출발역과 도착역이 같으면 PathFinder 생성에 실패한다.")
    void createPathFinder_sameSourceAndTarget_throwException() {
        // given
        Station station = new Station (1L, "교대역");
        PathFinder pathFinder = new PathFinder(createInitialSections());

        // when & then
        assertThatThrownBy(() -> pathFinder.searchShortestPath(station, station))
            .hasMessage("출발역과 도착역은 달라야 합니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @Test
    @DisplayName("출발역과 도착역이 연결 되어 있지 않으면 PathFinder 를 생성에 실패한다.")
    void createPathFinder_notConnectedSourceAndTarget_throwException() {
        // given
        List<Section> sections = createInitialSections();

        // when & then
        assertThatThrownBy(() -> new PathFinder(sections))
            .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @Test
    @DisplayName("존재하지 않는 출발역이나 도착역을 조회 할 경우 경로 조회에 실패한다.")
    void searchShortestPath_notExistSourceOrTarget_throwException() {
        // given
        Station existSource = new Station (1L, "교대역");
        Station notExistTarget = new Station (5L, "종합운동장역");
        PathFinder pathFinder = new PathFinder(createInitialSections());

        // when & then
        assertThatThrownBy(() -> pathFinder.searchShortestPath(existSource, notExistTarget))
            .hasMessage("출발역 또는 도착역이 존재하지 않습니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @Test
    @DisplayName("출발역부터 도착역까지의 최단경로와 거리를 반환한다.")
    void searchShortestPath_returnShortestPathAndDistance() {
        // given
        Station source = new Station (1L, "교대역");
        Station middle = new Station(4L, "남부터미널역");
        Station target = new Station (3L, "양재역");
        PathFinder pathFinder = new PathFinder(createInitialSections());
        List<Station> shortestPathStations = Arrays.asList(
            source,
            middle,
            target
        );

        // when
        PathResponse response = pathFinder.searchShortestPath(source, target);

        // then
        List<Station> actualStations = response.getStations().stream()
            .map(stationResponse -> new Station(stationResponse.getId(), stationResponse.getName()))
            .collect(Collectors.toList());
        assertThat(actualStations).isEqualTo(shortestPathStations);
        assertThat(response.getDistance()).isEqualTo(5);
    }

    private List<Station> createInitialStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1L, "교대역"));
        stations.add(new Station(2L, "강남역"));
        stations.add(new Station(3L, "양재역"));
        stations.add(new Station(4L, "남부터미널역"));
        return stations;
    }

    private List<Line> createInitialLines() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line(1L, "이호선", "초록색"));
        lines.add(new Line(2L, "신분당선", "빨간색"));
        lines.add(new Line(3L, "삼호선", "주황색"));
        return lines;
    }

    private List<Section> createInitialSections() {
        List<Line> lines = createInitialLines();
        List<Station> stations = createInitialStations();
        List<Section> sections = new ArrayList<>();

        /**
         * 교대역    --- *2호선* ---   강남역
         * |                        |
         * *3호선*                   *신분당선*
         * |                        |
         * 남부터미널역  --- *3호선* ---   양재
         */
        sections.add(new Section(1L, lines.get(0), stations.get(0), stations.get(1), 10));
        sections.add(new Section(1L, lines.get(1), stations.get(1), stations.get(2), 10));
        sections.add(new Section(1L, lines.get(2), stations.get(0), stations.get(3), 2));
        sections.add(new Section(1L, lines.get(2), stations.get(3), stations.get(2), 3));
        return sections;
    }
}