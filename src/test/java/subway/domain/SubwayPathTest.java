package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.fixture.LineFixture;
import subway.domain.fixture.StationFixture;

@DisplayName("SubwayPath 단위 테스트")
class SubwayPathTest {

    Line lineA;
    Line lineB;
    Line lineC;
    Line lineD;
    Station stationA;
    Station stationB;
    Station stationC;
    Station stationD;

    @BeforeEach
    void setUp() {
        stationA = StationFixture.createStationA();
        stationB = StationFixture.createStationB();
        stationC = StationFixture.createStationC();
        stationD = StationFixture.createStationD();

        lineA = LineFixture.createLineA();
        lineB = LineFixture.createLineB();
        lineC = LineFixture.createLineC();
        lineD = LineFixture.createLineD();
    }

    /*
                       10
          stationA    --- *lineA* ---  stationB
          |                        |
     3   *lineD*                   *lineB*  10
          |                        |
          stationD  --- *lineC* ---   stationC
                       10
     */
    @Test
    @DisplayName("지하철 최단경로를 계산한다")
    void getShortestDistance() {
        LineSections lineSectionsA = new LineSections(lineA,
            new Section(1L, lineA, stationA, stationB, 10));
        LineSections lineSectionsB = new LineSections(lineB,
            new Section(2L, lineB, stationB, stationC, 10));
        LineSections lineSectionsC = new LineSections(lineC,
            new Section(3L, lineC, stationC, stationD, 10));
        LineSections lineSectionsD = new LineSections(lineD,
            new Section(4L, lineD, stationD, stationA, 3));

        SubwayPath subwayPath = new SubwayPath(
            List.of(lineSectionsA, lineSectionsB, lineSectionsC, lineSectionsD));

        assertThat(subwayPath.calculateShortestPath(stationC, stationA).getStations()).containsExactly(stationC,
            stationD, stationA);

        assertThat(subwayPath.calculateShortestPath(stationC, stationA).getDistance()).isEqualTo(13);
    }

    @Test
    @DisplayName("path 없음")
    void noPath() {
        LineSections lineSectionsA = new LineSections(lineA,
            new Section(1L, lineA, stationA, stationB, 10));
        LineSections lineSectionsC = new LineSections(lineC,
            new Section(3L, lineC, stationC, stationD, 10));

        SubwayPath subwayPath = new SubwayPath(List.of(lineSectionsA, lineSectionsC));

        assertThatThrownBy(() -> subwayPath.calculateShortestPath(stationC, stationA)).isInstanceOf(
            IllegalArgumentException.class);
    }

    /*
    source station 없음
    dest station 없음
    source-dest가 같음 -> 1 station , distance 0
    해당 부분을 검증해볼것
     */
}
