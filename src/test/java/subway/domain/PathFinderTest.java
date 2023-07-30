package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import subway.DomainFixture;
import subway.exception.PathException;
import subway.exception.StationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class PathFinderTest {

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
        station5 = new Station(5L, "station5");
        station6 = new Station(6L, "station6");
    }

    @Nested
    @DisplayName("findPath 메소드는")
    class FindPath_Method {

        @Test
        @DisplayName("startStation이 null이면, StationException을 던진다")
        void Throw_StationException_When_startStation_Null() {
            // given
            Station nullStation = null;
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);

            // when
            Exception exception = catchException(() -> new PathFinder(List.of(section1), nullStation, station1));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("endStation이 null이면, StationException을 던진다")
        void Throw_StationException_When_endStation_Null() {
            // given
            Station nullStation = null;
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);

            // when
            Exception exception = catchException(() -> new PathFinder(List.of(section1), station1, nullStation));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("startStation과 endStation이 같을 경우, PathException을 던진다")
        void Throw_PathException_When_Same_Stations() {
            // given
            Station SameStation = station1;
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);

            // when
            Exception exception = catchException(() -> new PathFinder(List.of(section1), SameStation, SameStation));

            // then
            assertThat(exception).isInstanceOf(PathException.class);
        }

        @Test
        @DisplayName("startStation과 endStation이 연결되어있지 않을 경우, PathException을 던진다")
        void Throw_PathException_When_UnLinked_Stations() {
            // given
            Station startStation = station1;
            Station endStation = station4;

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station3, station4, 15);

            // when
            Exception exception = catchException(() -> new PathFinder(List.of(section1, section2), startStation, endStation));

            // then
            assertThat(exception).isInstanceOf(PathException.class);
        }

        /** startStation -- 10(line1) -- station2 -- 10(line1) -- station3 -- 10(line1) -- endStation **/
        @Test
        @DisplayName("startStation과 endStation이 하나의 노선에 존재할 경우, 경로와 거리를 계산한다")
        void Calculate_Path_And_Distance_When_Same_Line() {
            // given
            Station startStation = station1;
            Station endStation = station4;

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 10);
            Section section3 = DomainFixture.Section.buildWithStations(station3, station4, 10);

            // whe
            PathFinder pathFinder = new PathFinder(List.of(section1, section2, section3), startStation, endStation);

            List<Station> stations = pathFinder.getPath();
            Long distance = pathFinder.getDistance();

            // then
            assertThat(stations).containsExactly(startStation, station2, station3, endStation);
            assertThat(distance).isEqualTo(30);
        }

        /**
         * station1 --- 10(line2) --- station2
         * |                          |
         * 12(line3)                  15(line1)
         * |                          |
         * station3 --- 12(line3) --- station4
         */
        @Test
        @DisplayName("startStation과 endStation이 다른 노선에 존재할 경우, 경로와 거리를 계산한다")
        void Calculate_Path_And_Distance_When_Different_Line() {
            // given
            Station startStation = station1;
            Station endStation = station4;

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station4, 15);
            Section section3 = DomainFixture.Section.buildWithStations(station3, station4, 12);
            Section section4 = DomainFixture.Section.buildWithStations(station1, station3, 12);
            // whe
            PathFinder pathFinder = new PathFinder(List.of(section1, section2, section3, section4), startStation, endStation);

            List<Station> stations = pathFinder.getPath();
            Long distance = pathFinder.getDistance();

            // then
            assertThat(stations).containsExactly(startStation, station3, endStation);
            assertThat(distance).isEqualTo(24);
        }

    }
}
