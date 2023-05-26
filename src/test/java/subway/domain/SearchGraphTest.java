package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.searchGraph.JgraphtSearchGraph;
import subway.domain.searchGraph.SearchGraph;
import subway.domain.vo.Distance;
import subway.domain.vo.SubwayPath;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SearchGraphTest {
    SearchGraph searchGraph = new JgraphtSearchGraph();

    private Station chungjeong = new Station(1L, "충정로");
    private Station sichung = new Station(2L, "시청");
    private Station uljiro3 = new Station(3L, "을지로 3가");
    private Station seoulyuk = new Station(4L, "서울역");
    private Station jongro3 = new Station(5L, "종로 3가");
    private Station uljiro4 = new Station(6L, "을지로 4가");

    @BeforeEach
    void init() {
        searchGraph = new JgraphtSearchGraph();
    }

    @Test
    @DisplayName("탐색그래프에 역을 추가한다.")
    void addStation() {
        // when
        searchGraph.addStation(chungjeong);
        searchGraph.addStation(sichung);
        searchGraph.addStation(uljiro3);

        // then
        assertThat(searchGraph.getStations().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("탐색그래프에 구간을 추가한다.")
    void addSection() {
        // given
        searchGraph.addStation(chungjeong);
        searchGraph.addStation(sichung);
        int distance = 10;

        // when
        Section section = Section.builder()
                .id(1L)
                .line(new Line(1L, "1호선"))
                .upStation(chungjeong)
                .downStation(sichung)
                .distance(new Distance(distance))
                .build();
        searchGraph.addSection(section);

        // then
        SubwayPath shortenPath = searchGraph.getShortenPath(chungjeong, sichung);
        SubwayPath answer = SubwayPath.of(List.of(chungjeong, sichung), distance);

        assertThat(shortenPath).isEqualTo(answer);
    }

    @Test
    @DisplayName("최단거리를 구한다.")
    void getShortenPath() {
        // given
        // vertex (station)
        searchGraph.addStation(chungjeong);
        searchGraph.addStation(sichung);
        searchGraph.addStation(uljiro3);
        searchGraph.addStation(seoulyuk);
        searchGraph.addStation(jongro3);
        searchGraph.addStation(uljiro4);

        // edge (section)
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), chungjeong, sichung, new Distance(1)));
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), sichung, uljiro3, new Distance(1)));
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), uljiro3, uljiro4, new Distance(10)));
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), seoulyuk, sichung, new Distance(2)));
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), sichung, jongro3, new Distance(2)));
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), jongro3, uljiro4, new Distance(2)));

        // when
        SubwayPath shortenPath = searchGraph.getShortenPath(sichung, uljiro4);

        // then
        SubwayPath answer = SubwayPath.of(List.of(sichung, jongro3, uljiro4), 4);
        assertThat(shortenPath).isEqualTo(answer);
    }
}
