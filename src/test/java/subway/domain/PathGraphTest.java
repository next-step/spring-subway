package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static subway.domain.fixture.LineFixture.createLine;
import static subway.domain.fixture.SectionFixture.createSection;
import static subway.domain.fixture.StationFixture.createStation;

class PathGraphTest {

    private Line lineA;
    private Line lineB;
    private Line lineC;

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;
    private Station stationF;

    private Section sectionA;
    private Section sectionB;
    private Section sectionC;
    private Section sectionD;
    private Section sectionE;
    private Section sectionF;

    /**
     * StationA                 StationF
     * |                        |
     * *LineA*                   *LineA*
     * |                        |
     * StationB  --- *LineA* ---StationC
     * |                        |
     * *LineB*                  *LineC*
     * |                        |
     * StationD --- *LineB* --- StationE
     */

    @BeforeEach
    void setUp() {
        lineA = createLine("2호선");
        lineB = createLine("3호선");
        lineC = createLine("7호선");

        stationA = createStation(1L, "낙성대");
        stationB = createStation(2L, "사당");
        stationC = createStation(3L, "방배");
        stationD = createStation(4L, "서초");
        stationE = createStation(5L, "교대");
        stationF = createStation(6L, "잠실");

        sectionA = createSection(1L, lineA, stationA, stationB, 10);
        sectionB = createSection(2L, lineA, stationB, stationC, 20);
        sectionC = createSection(3L, lineB, stationB, stationD, 5);
        sectionD = createSection(4L, lineB, stationD, stationE, 10);
        sectionE = createSection(2L, lineA, stationC, stationF, 15);
        sectionF = createSection(2L, lineC, stationC, stationE, 1);
    }

    @Test
    @DisplayName("전체 구간에 대한 정보가 주어졌을 때 시작역부터 도착역까지 가장 짦은 거리로 갈 수 있는 역 목록을 반환한다.")
    void getShortPathTest() {
        // given
        final WholeSection wholeSection = new WholeSection(List.of(sectionA, sectionB, sectionC, sectionD, sectionE));
        final PathGraph graph = new PathGraph(wholeSection);

        // when
        final ShortPath shortPath = graph.getShortPath(stationF, stationD);

        // then
        assertThat(shortPath.getDistance()).isEqualTo(40);
        assertThat(shortPath.getStations()).hasSize(4)
                .containsExactly(stationF, stationC, stationB, stationD);

    }

    @Test
    @DisplayName("전체 구간에 대한 정보가 주어졌을 때 시작역부터 도착역까지 더 짦은 거리로 갈 수 있는 역이 생긴 경우 더 짧은 길을 선택하여 역 목록을 반환한다.")
    void getShortPathTest2() {
        // given
        final WholeSection wholeSection = new WholeSection(List.of(sectionA, sectionB, sectionC, sectionD, sectionE, sectionF));
        final PathGraph graph = new PathGraph(wholeSection);

        // when
        final ShortPath shortPath = graph.getShortPath(stationF, stationD);

        // then
        assertThat(shortPath.getDistance()).isEqualTo(26);
        assertThat(shortPath.getStations()).hasSize(4)
                .containsExactly(stationF, stationC, stationE, stationD);

    }

    @Test
    @DisplayName("전체 구간에 대한 정보가 주어졌을 때 시작역과 도착역이 같은 경우 예외를 던진다.")
    void SourceEqualsTargetThenThrow() {
        // given
        final WholeSection wholeSection = new WholeSection(List.of(sectionA, sectionB, sectionC, sectionD, sectionE, sectionF));
        final PathGraph graph = new PathGraph(wholeSection);

        // when , then
        assertThatCode(() -> graph.getShortPath(stationA, stationA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 같은 경우 최단 거리를 구할 수 없습니다.");
    }

    @Test
    @DisplayName("전체 구간에 대한 정보가 주어졌을 때 시작역과 도착역이 연결되어 있지 않은 경우 예외를 던진다.")
    void SourceNotConnectionTargetThenThrow() {
        // given
        final WholeSection wholeSection = new WholeSection(List.of(sectionA, sectionB, sectionC, sectionD, sectionE, sectionF));
        final PathGraph graph = new PathGraph(wholeSection);
        final Station station = createStation("어린이대공원역");
        // when , then
        assertThatCode(() -> graph.getShortPath(stationA, station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }
}
