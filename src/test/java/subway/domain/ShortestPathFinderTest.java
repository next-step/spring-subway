package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShortestPathFinderTest {

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 경로를 구할 수 없다.")
    void cannotFindPathWithSameSourceAndTarget() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 4);

        //when & then
        assertThatThrownBy(() -> new ShortestPathFinder(List.of(sectionA, sectionB), stationA, stationA))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 경로를 구할 수 없다.")
    void cannotFindPathWithDisconnectedSourceAndTarget() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Line lineB = new Line(2L, "B", "green");

        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineB, stationC, stationD, 4);

        //when & then
        assertThatThrownBy(() -> new ShortestPathFinder(List.of(sectionA, sectionB), stationA, stationD))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("최단 경로를 구한다.")
    void findShortestPath() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Line lineB = new Line(2L, "B", "green");

        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");
        Station stationE = new Station(5L, "E");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 2);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 2);
        Section sectionC = new Section(3L, lineA, stationC, stationD, 2);
        Section sectionD = new Section(4L, lineA, stationD, stationE, 2);

        Section sectionE = new Section(5L, lineB, stationA, stationC, 5);
        Section sectionF = new Section(6L, lineB, stationC, stationE, 3);

        //when
        final ShortestPathFinder pathFinder = new ShortestPathFinder(
            List.of(sectionA, sectionB, sectionC, sectionD, sectionE, sectionF), stationA,
            stationE);

        //then
        assertThat(pathFinder.getStations()).isEqualTo(
            List.of(stationA, stationB, stationC, stationE));
        assertThat(pathFinder.getDistance()).isEqualTo(7);
    }
}
