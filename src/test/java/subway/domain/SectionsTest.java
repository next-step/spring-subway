package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionsTest {

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


    @Test
    @DisplayName("Sections 가 적어도 하나의 Section 을 가지지 않으면 예외를 던진다.")
    void SectionsSizeValidation() {
        assertThatCode(() -> new Sections(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간은 반드시 한개 이상이어야합니다.");
    }

    @DisplayName("여러 개의 Section 정보가 있을 때 역을 반환 , 순서가 보장되지 않는다.")
    @Test
    void givenManySectionsWhenToStationsThenOrderedStations() {
        // given
        final List<Section> sectionList = List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        );
        final SortedSections sortedSections = new SortedSections(sectionList);
        // when

        final List<Station> stations = sortedSections.toStations();

        // then
        assertThat(stations).containsAnyOf(stationD, stationA, stationB, stationC);
    }

    @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹칠 때 ")
    @Test
    void A_D_B_C() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationA, stationD, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationD, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationD, stationB, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("추가구간의 상행역과 기존 구간의 하행역이 겹칠 때 ")
    @Test
    void A_B_C_D() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationC, stationD, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB, stationC);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationC, stationD);
        assertThat(result).hasSize(3);
    }

    @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠 때 ")
    @Test
    void A_B_D_C() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationD, stationC, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB, stationD);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationD, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("추가구간의 하행역과 기존 구간의 상행역이 겹칠 때 ")
    @Test
    void D_A_B_C() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationD, stationA, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationD, stationA, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationA, stationB, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("역사이에 역 등록시 구간이 기존 구간보다 크거나 같으면 등록시 예외를 던진다.")
    @Test
    void addSectionTooMuchDistanceThenThrow() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when , then
        assertThatCode(() -> sections.addSection(new Section(line, stationA, stationD, new Distance(100L))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 1이상이어야 합니다.");

    }

    @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재할 시 예외를 던진다.")
    @Test
    void addSectionStationsAlreadyExistInLineThenThrow() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when , then
        assertThatCode(() -> sections.addSection(new Section(line, stationB, stationA, new Distance(5L))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재할 시 예외를 던진다.")
    @Test
    void addSectionStationsNotExistInLineThenThrow() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when , then
        assertThatCode(() -> sections.addSection(new Section(line, stationE, stationD, new Distance(5L))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }


}
