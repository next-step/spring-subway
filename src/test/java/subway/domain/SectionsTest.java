package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("빈 리스트로 Sections 생성시 빈 리스트 반환")
    @Test
    void emptySectionsThenEmptyStation() {
        // given
        List<Section> sectionList = List.of();

        // when
        Sections sections = new Sections(sectionList);

        // then
        assertThat(sections.toStations()).isEmpty();
    }

    @DisplayName("여러 개의 Section 정보가 있을 때 정렬된 순서로 역을 반환")
    @Test
    void givenManySectionsWhenToStationsThenOrderedStations() {
        // given

        Line line = new Line(1L, "1호선", "green");
        Station station1 = new Station(1L, "낙성대");
        Station station2 = new Station(2L, "사당");
        Station station3 = new Station(3L, "이수");
        Station station4 = new Station(4L, "잠실");

        List<Section> sectionList = List.of(
                new Section(line, station1, station2, new Distance(10L)),
                new Section(line, station2, station3, new Distance(10L)),
                new Section(line, station4, station1, new Distance(10L))
        );
        Sections sections = new Sections(sectionList);
        // when

        List<Station> stations = sections.toStations();

        // then
        assertThat(stations).containsExactly(station4, station1, station2, station3);
    }

}
