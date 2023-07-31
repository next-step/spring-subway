package subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ShortestPathTest {

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

        final ShortestPathFinder pathFinder = new ShortestPathFinder() {
            @Override
            public void calculatePath(final List<Section> sections, final Station source,
                final Station target) {
            }

            @Override
            public List<Station> getStations() {
                return List.of(stationA);
            }

            @Override
            public int getDistance() {
                return 0;
            }
        };

        //when & then
        assertThatThrownBy(
            () -> new ShortestPath(pathFinder, List.of(sectionA, sectionB), stationA, stationA))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
