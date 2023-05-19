package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    @DisplayName("그래프에서 구간을 제거한다.")
    void removeSectionToGraph() {
        // given
        Line line = new Line(1L, "1");
        Station upStation = new Station(1L);
        Station downStationId = new Station(2L);
        Integer distance = 10;
        Section section = Section.of(line, upStation, downStationId, distance);

        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(section);

        // when
        subwayGraph.remove(2L);

        // then
        assertThat(subwayGraph.getSections(upStation)).doesNotContain(section);
    }

    @Test
    @DisplayName("삭제하려는 구간이 없어서 실패합니다.")
    void removeFailBecauseOfNotExists() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        Long removeStationId = 10L;
        System.out.println(subwayGraph.getConnection());
        // when
        assertThatThrownBy(() -> {
            subwayGraph.remove(removeStationId);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("삭제할 역이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("삭제하려는 구간의 역이 하행종점역이 아니라서 실패합니다.")
    void removeFailBecauseOfNotLastStation() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        Long removeStationId = 3L;
        System.out.println(subwayGraph.getConnection());
        // when
        assertThatThrownBy(() -> {
            subwayGraph.remove(removeStationId);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessage("하행 종점역만 제거할 수 있습니다.");
    }

    @Test
    @DisplayName("노선의 하행 종점역 구간 삭제를 성공합니다.")
    void removeSectionToLastDownStationSuccessfully() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        Section sectionWillBeRemoved = makeSection(1L, 3L, 4L, 1);
        subwayGraph.add(sectionWillBeRemoved);

        Long removeStationId = 4L;

        // when
        subwayGraph.remove(removeStationId);

        // then
        assertThat(subwayGraph.getSections(new Station(3L))).doesNotContain(sectionWillBeRemoved);
    }

    @Test
    @DisplayName("호선에 해당하는 역을 전부 반환합니다")
    void getStationsInLineSuccess() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        Line line = new Line(1L, "1호선");

        // when
        List<Station> stationsInLine = subwayGraph.getStationsInLine(line);

        // then
        assertThat(stationsInLine).hasSize(4);
        List<Station> result = List.of(new Station(1L), new Station(2L), new Station(3L), new Station(4L));
        assertThat(stationsInLine).isEqualTo(result);
    }

    @Test
    @DisplayName("호선에 해당하는 역이 없을 경우 빈 리스트을 반환합니다.")
    void getStationsInLineSuccessReturnEmptyList() {
        // given
        SubwayGraph subwayGraph = new SubwayGraph();
        subwayGraph.add(makeSection(1L, 1L, 2L, 1));
        subwayGraph.add(makeSection(1L, 2L, 3L, 1));
        subwayGraph.add(makeSection(1L, 3L, 4L, 1));

        Line line = new Line(2L, "2호선");

        // when
        List<Station> stationsInLine = subwayGraph.getStationsInLine(line);

        // then
        assertThat(stationsInLine).hasSize(0);
    }

    private static Section makeSection(long lineId, long upStationId, long downStationId, Integer distance) {
        Line line = new Line(lineId, "name");
        Station upStation = new Station(upStationId);
        Station downStation = new Station(downStationId);

        return Section.of(line, upStation, downStation, distance);
    }
}
