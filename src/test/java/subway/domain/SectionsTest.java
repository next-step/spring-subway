package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionsTest {

    @Test
    @DisplayName("Sections 가 적어도 하나의 Section 을 가지지 않으면 예외를 던진다.")
    void SectionsSizeValidation() {
        assertThatCode(() -> new Sections(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간은 반드시 한개 이상이어야합니다.");
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

    @DisplayName("Sections 의 가장 마지막 Section 을 삭제한다.")
    @Test
    void givenSectionsWhenFindLastSectionThenReturnLastSection() {
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
        final Section lastSection = sections.deleteLastSection();

        // then
        assertThat(lastSection).isEqualTo(new Section(line, station2, station3, new Distance(10L)));
        assertThat(sections.sectionLength()).isEqualTo(2);
    }
}
