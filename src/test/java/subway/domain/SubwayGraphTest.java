package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 그래프")
class SubwayGraphTest {

    @Test
    @DisplayName("구간 추가에 성공합니다")
    void addSectionSuccessfully() {
        // given
        Line line = new Line(1L, "1");
        Station upStation = new Station(1L);
        Station downStationId = new Station(2L);
        Integer distance = 10;

        Section section = Section.of(line, upStation, downStationId, distance);

        // when
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(section);

        // then
        assertThat(subwayGraph.getSections(upStation)).hasSize(1);
        assertThat(subwayGraph.getSections(upStation)).contains(section);
    }

    @Test
    @DisplayName("추가하려는 구간의 역이 하행종점역이 아니라서 실패합니다.")
    void addFailBecauseOfNotLastStation() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        Section addSection = makeSection(1L, 2L, 5L, 1);

        // when
        assertThatThrownBy(() -> {
            subwayGraph.add(addSection);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("기존의 하행 종점역에만 추가할 수 있습니다.");
    }

    @Test
    @DisplayName("추가하려는 역(구간의 하행역)이 노선에 이미 있어서 실패합니다.")
    void addFailBecauseOfAlreadyExists() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        System.out.println(subwayGraph.getFirstStationInLines());

        Section addSection = makeSection(1L, 4L, 1L, 1);

        // when
        assertThatThrownBy(() -> {
            subwayGraph.add(addSection);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 해당 노선에 존재하는 역은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("노선의 하행 종점역에 구간 추가를 성공합니다.")
    void addSectionToLastDownStationSuccessfully() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        Section addSection = makeSection(1L, 4L, 5L, 1);

        // when
        subwayGraph.add(addSection);

        // then
        assertThat(subwayGraph.getSections(addSection.getUpStation())).hasSize(1);
        assertThat(subwayGraph.getSections(addSection.getUpStation())).contains(addSection);
    }

    private static Section makeSection(long lineId, long stationId, long downStationId, Integer distance) {
        Line line = new Line(lineId, "name");
        Station upStation = new Station(stationId);
        Station downStation = new Station(downStationId);

        return Section.of(line, upStation, downStation, distance);
    }
}
