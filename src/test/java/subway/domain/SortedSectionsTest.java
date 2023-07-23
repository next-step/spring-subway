package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SortedSectionsTest {

    private Line line;
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "1호선", "green");
        stationA = new Station(1L, "낙성대");
        stationB = new Station(2L, "사당");
        stationC = new Station(3L, "이수");
        stationD = new Station(4L, "잠실");
        stationE = new Station(5L, "신촌");
    }

    @DisplayName("여러 개의 Section 정보가 있을 때 정렬된 순서로 역을 반환")
    @Test
    void givenManySectionsWhenToStationsThenOrderedStations() {
        // given
        final List<Section> sectionList = List.of(
                new Section(line, stationA, stationB, new Distance(10L)),
                new Section(line, stationB, stationC, new Distance(10L)),
                new Section(line, stationD, stationA, new Distance(10L))
        );
        final SortedSections sortedSections = new SortedSections(sectionList);
        // when

        final List<Station> stations = sortedSections.toStations();

        // then
        assertThat(stations).containsExactly(stationD, stationA, stationB, stationC);
    }

    @DisplayName("Sections 의 가장 마지막 Section 을 삭제한다.")
    @Test
    void givenSectionsWhenFindLastSectionThenReturnLastSection() {
        // given
        final List<Section> sectionList = List.of(
                new Section(line, stationA, stationB, new Distance(10L)),
                new Section(line, stationB, stationC, new Distance(10L)),
                new Section(line, stationD, stationA, new Distance(10L))
        );
        final SortedSections sortedSections = new SortedSections(sectionList);

        // when
        final Section lastSection = sortedSections.deleteLastSection();

        // then
        assertThat(lastSection).isEqualTo(new Section(line, stationB, stationC, new Distance(10L)));
        assertThat(sortedSections.sectionLength()).isEqualTo(2);
    }
}