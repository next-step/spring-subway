package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 그래프")
class SubwayGraphTest {

    @Test
    @DisplayName("그래프에 구간을 하나 추가한다.")
    void addSectionToGraph(){
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
        assertThat(subwayGraph.getSections(upStation).get(0)).isEqualTo(section);
    }
}
