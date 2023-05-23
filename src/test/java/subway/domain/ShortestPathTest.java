package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.shortestpath.ShortestPathSameStationException;

class ShortestPathTest {

    private List<Station> stations;

    private List<Section> sections;

    private Station 신사역;

    private Station 논현역;

    private Station 신논현역;

    private Station 판교역;

    @BeforeEach
    void setUp() {
        this.신사역 = new Station(1L, "신사");
        this.논현역 =  new Station(2L, "논현");
        this.신논현역 = new Station(3L, "신논현");
        this.판교역 = new Station(4L, "판교");
        this.stations = List.of(신사역, 논현역, 신논현역, 판교역);

        Line 신분당선 = new Line(1L, "신분당선", "Red");
        Line 경강선 = new Line(2L, "경강선", "Blue");
        this.sections = List.of(new Section(1L, 신분당선, 신사역, 논현역, 1),
            new Section(2L, 신분당선, 논현역, 신논현역, 1),
            new Section(3L, 경강선, 논현역, 판교역, 2));
    }

    @DisplayName("역들과 노선들이 있을 때 출발역과 도착역을 입력하여 최단거리 경로를 구하면 최단거리 경로를 반환한다.")
    @Test
    void getPaths() {
        // given
        ShortestPath shortestPath = new ShortestPath(stations, sections);

        // when
        List<Station> paths = shortestPath.getPaths(신사역, 판교역);

        // then
        assertThat(paths.size()).isEqualTo(3);
    }

    @DisplayName("역들과 노선들이 있고 출발역과 도착역이 같을때 최단거리 경로를 구하면 에러를 반환한다.")
    @Test
    void getPathsFalse() {
        // given
        ShortestPath shortestPath = new ShortestPath(stations, sections);

        // when

        // then
        assertThrows(ShortestPathSameStationException.class, () -> shortestPath.getPaths(신사역, 신사역));
    }

    @DisplayName("역들과 노선들이 있을 때 출발역과 도착역을 입력하여 총 거리를 구하면 총 거리를 반환한다.")
    @Test
    void getDistance() {
        // given
        ShortestPath shortestPath = new ShortestPath(stations, sections);

        // when
        int distance = shortestPath.getDistance(신사역, 판교역);

        // then
        assertThat(distance).isEqualTo(3);
    }

    @DisplayName("역들과 노선들이 있고 출발역과 도착역이 같을때 총 거리를 구하면 에러를 반환한다.")
    @Test
    void getDistanceFalse() {
        // given
        ShortestPath shortestPath = new ShortestPath(stations, sections);

        // when

        // then
        assertThrows(ShortestPathSameStationException.class, () -> shortestPath.getDistance(신사역, 신사역));
    }
}
