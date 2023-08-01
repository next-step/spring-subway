package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.DomainFixture;
import subway.application.PathFinder;
import subway.exception.PathException;
import subway.vo.Path;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PathFinder.class)
@DisplayName("PathFinder 클래스")
public class PathFinderTest {

    @Autowired
    private PathFinder pathFinder;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
    }

    @Nested
    @DisplayName("findPath 메소드는")
    class FindPath_Method {

        @Test
        @DisplayName("startStation과 endStation이 연결되어있지 않을 경우, PathException을 던진다")
        void Throw_PathException_When_UnLinked_Stations() {
            // given
            Station startStation = station1;
            Station endStation = station4;

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station3, station4, 15);

            // when
            Exception exception = catchException(() -> pathFinder.findPath(List.of(section1, section2), startStation, endStation));

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

            // when
            Path path = pathFinder.findPath(List.of(section1, section2, section3), startStation, endStation);

            // then
            List<Station> expectedStations = Arrays.asList(startStation, station2, station3, endStation);

            assertThat(path.getStations()).containsAll(expectedStations);
            assertThat(path.getDistance()).isEqualTo(30);
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

            // when
            Path path = pathFinder.findPath(List.of(section1, section2, section3, section4), startStation, endStation);

            // then

            List<Station> expectedStations = Arrays.asList(startStation, station3, endStation);
            assertThat(path.getStations()).isEqualTo(expectedStations);
            assertThat(path.getDistance()).isEqualTo(24);
        }

    }
}
