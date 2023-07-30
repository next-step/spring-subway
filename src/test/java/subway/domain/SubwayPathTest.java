package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.fixture.LineFixture;
import subway.domain.fixture.StationFixture;
import subway.dto.response.StationResponse;

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
    void calculateShortestPath() {
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

        assertThat(subwayPath.calculateShortestPath(stationC, stationA).getStations()).isEqualTo(StationResponse.listOf(List.of(stationC,
            stationD, stationA)));

        assertThat(subwayPath.calculateShortestPath(stationC, stationA).getDistance()).isEqualTo(13);
    }

    @Test
        @DisplayName("경로가 존재하지 않으면 IllegalArgumentException을 던진다")
    void throwIllegalArgumentExceptionIfPathDoesNotExist() {
        LineSections lineSectionsA = new LineSections(lineA,
            new Section(1L, lineA, stationA, stationB, 10));
        LineSections lineSectionsC = new LineSections(lineC,
            new Section(3L, lineC, stationC, stationD, 10));

        SubwayPath subwayPath = new SubwayPath(List.of(lineSectionsA, lineSectionsC));

        assertThatThrownBy(() -> subwayPath.calculateShortestPath(stationC, stationA)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @Test
    @DisplayName("출발역과 도착역이 같으면 거리가 0, 출발역 하나를 반환한다")
    void calculateShortestPathBothSameSourceAndDestination() {
        LineSections lineSectionsA = new LineSections(lineA,
            new Section(1L, lineA, stationA, stationB, 10));

        SubwayPath subwayPath = new SubwayPath(List.of(lineSectionsA));

        assertThat(subwayPath.calculateShortestPath(stationA, stationA).getStations()).containsExactly(
            StationResponse.of(stationA));
        assertThat(subwayPath.calculateShortestPath(stationA, stationA).getDistance()).isEqualTo(0d);
    }

    @Test
    @DisplayName("등록되지 않은 출발, 도착역으로는 경로를 계산할 수 없다.")
    void cannotCalculateShortestPathWithUnregisteredStation() {
        LineSections lineSectionsA = new LineSections(lineA,
            new Section(1L, lineA, stationA, stationB, 10));
        Station unregisteredStation = StationFixture.createStation("none");

        SubwayPath subwayPath = new SubwayPath(List.of(lineSectionsA));

        assertThatThrownBy(() -> subwayPath.calculateShortestPath(unregisteredStation, stationA))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> subwayPath.calculateShortestPath(stationA, unregisteredStation))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
