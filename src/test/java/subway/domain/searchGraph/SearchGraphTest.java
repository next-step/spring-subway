package subway.domain.searchGraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.vo.Distance;
import subway.domain.vo.SubwayPath;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static subway.domain.searchGraph.SearchGraphErrorMessage.*;

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
        SubwayPath shortenPath = searchGraph.findShortenPath(chungjeong, sichung);
        SubwayPath answer = SubwayPath.of(List.of(chungjeong, sichung), distance);

        assertThat(shortenPath).isEqualTo(answer);
    }

    @Test
    @DisplayName("구간을 제거한다.")
    void removeSection() {
        // given
        searchGraph.addStation(chungjeong);
        searchGraph.addStation(sichung);
        int distance = 10;

        Section section = Section.builder()
                .id(1L)
                .line(new Line(1L, "1호선"))
                .upStation(chungjeong)
                .downStation(sichung)
                .distance(new Distance(distance))
                .build();
        searchGraph.addSection(section);

        // when
        searchGraph.removeSection(section);

        // then
        SubwayPath shortenPath = searchGraph.findShortenPath(chungjeong, sichung);
        SubwayPath answer = SubwayPath.of(Collections.emptyList(), 0);

        assertThat(shortenPath).isEqualTo(answer);
    }

    @Test
    @DisplayName("최단거리인 경로를 구한다.")
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
        SubwayPath shortenPath = searchGraph.findShortenPath(sichung, uljiro4);

        // then
        SubwayPath answer = SubwayPath.of(List.of(sichung, jongro3, uljiro4), 4);
        assertThat(shortenPath).isEqualTo(answer);
    }

    @Test
    @DisplayName("입력값에 해당하는 역이 없어서 최단 경로 찾기에 실패합니다.")
    void getShortenPathFailBecauseStationNotExists() {
        // given
        // vertex (station)
        searchGraph.addStation(chungjeong);
        searchGraph.addStation(sichung);
        searchGraph.addStation(uljiro3);
        searchGraph.addStation(seoulyuk);

        // edge (section)
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), chungjeong, sichung, new Distance(1)));
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), sichung, uljiro3, new Distance(1)));


        // when
        assertThatThrownBy(() -> {
            searchGraph.findShortenPath(sichung, uljiro4);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SEARCH_GRAPH_NOT_CONTAINS_STATION);
    }

    @Test
    @DisplayName("입력값에 해당하는 구간이 없어서 최단 경로 찾기에 실패합니다.")
    void getShortenPathFailBecauseSectionNot() {
        // given
        // vertex (station)
        searchGraph.addStation(chungjeong);
        searchGraph.addStation(sichung);
        searchGraph.addStation(uljiro3);
        searchGraph.addStation(seoulyuk);
        // edge (section)
        searchGraph.addSection(new Section(1L ,new Line(1L, "1"), chungjeong, sichung, new Distance(1)));
        searchGraph.addSection(new Section(2L ,new Line(1L, "1"), uljiro3, seoulyuk, new Distance(1)));

        // when
        assertThatThrownBy(() -> {
            searchGraph.findShortenPath(sichung, seoulyuk);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SEARCH_GRAPH_CANNOT_FIND_PATH);
    }
}
